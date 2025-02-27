package curb.core.mvc.proxy;

import curb.core.AccessLevel;
import curb.core.CurbAccessConfig;
import curb.core.configuration.ReverseProxyProperties;
import curb.core.util.ServletUtil;
import curb.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 简易反向代理处理器
 */
public class CurbReverseProxyHandlerMapping extends AbstractUrlHandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(CurbReverseProxyHandlerMapping.class);

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    private final ReverseProxyProperties properties;
    private final RestTemplate restTemplate;
    private final Set<String> ignoredRequestHeaders;
    private final Set<String> ignoredResponseHeaders;

    public CurbReverseProxyHandlerMapping(ReverseProxyProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.ignoredRequestHeaders = properties.ignoredRequestHeadersSet();
        this.ignoredResponseHeaders = properties.ignoredResponseHeadersSet();
        setOrder(properties.getOrder());
    }

    @Override
    public void initApplicationContext() {
        if (!properties.isEnabled()) {
            return;
        }
        List<ReverseProxyProperties.Route> routes = properties.getRoutes();
        if (routes == null || routes.isEmpty()) {
            log.warn("No route configuration found for reverse proxy.");
            return;
        }
        for (ReverseProxyProperties.Route route : routes) {
            if (!StringUtils.hasText(route.getServer())) {
                log.warn("Skip the incorrect route configuration {}: server field should not be empty.", route);
                continue;
            }
            if (CollectionUtils.isEmpty(route.getPathPatterns())) {
                log.warn("Skip the incorrect route configuration {}: path-patterns field should not be empty.", route);
                continue;
            }
            RouteHandler handler = new RouteHandler(route);
            String prefix = properties.getPrefix();
            for (String pathPattern : route.getPathPatterns()) {
                String urlPath = prefix + pathPattern;
                registerHandler(urlPath, handler);
            }
            log.info("Initializing route configuration: {} (prefix={})", route, prefix);
        }
        super.initApplicationContext();
    }

    /**
     * 判断请求是否匹配到反向代理路由
     *
     * @param request 请求对象
     * @return 是否匹配
     */
    public boolean isMatched(HttpServletRequest request) {
        try {
            return getHandlerInternal(request) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据配置解析访问控制参数
     */
    protected CurbAccessConfig resolveAccessConfig(HttpServletRequest request, ReverseProxyProperties.Route route) {
        AccessLevel accessLevel = properties.getAccessLevel();
        if (accessLevel == null) {
            accessLevel = AccessLevel.ANONYMOUS;
        }
        String path = null;
        if (accessLevel == AccessLevel.PERMISSION) {
            String raw = parseUriFromRequest(request);
            path = rewriteUri(raw, route).getPath();
        }
        return new AccessConfig(accessLevel, path);
    }

    /**
     * 转发时设置额外的请求头信息, 可根据需求在子类中覆盖实现
     */
    protected void setExtraHeader(ReverseProxyProperties.Route route, HttpServletRequest request, HttpHeaders upstreamHeaders) {
    }

    /**
     * 判断指定的请求头是否要在转发请求时忽略掉
     *
     * @param headerName 请求头名字
     * @return 是否忽略
     */
    private boolean ignoreRequestHeader(String headerName) {
        return ignoredRequestHeaders.contains(headerName.toLowerCase());
    }

    /**
     * 判断指定的响应头是否在转发响应时忽略掉
     *
     * @param headerName 响应头名字
     * @return 是否忽略
     */
    private boolean ignoreResponseHeader(String headerName) {
        return ignoredResponseHeaders.contains(headerName.toLowerCase());
    }

    private String parseUriFromRequest(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isEmpty()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }

    private URI rewriteUri(String uri, ReverseProxyProperties.Route route) {
        uri = stripPrefix(uri);
        String server = route.getServer();
        String rewrite;
        if (server.endsWith("/")) {
            rewrite = server + uri;
        } else {
            rewrite = server + "/" + uri;
        }
        return URI.create(rewrite);
    }

    private String stripPrefix(String uri) {
        String prefix = properties.getPrefix();
        if (StringUtil.isBlank(prefix)) {
            return uri;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        if (uri.startsWith(prefix)) {
            return uri.substring(prefix.length());
        }
        return uri;
    }

    /**
     * 访问控制配置
     */
    private static class AccessConfig implements CurbAccessConfig {

        private final AccessLevel accessLevel;
        private final String path;

        public AccessConfig(AccessLevel accessLevel, String path) {
            this.accessLevel = accessLevel;
            this.path = path;
        }

        @Override
        public AccessLevel getLevel() {
            return accessLevel;
        }

        @Override
        public String getSign() {
            return null;
        }

        @Override
        public String getPath() {
            return path;
        }
    }

    /**
     * 指定路由的转发处理器
     */
    public class RouteHandler implements HttpRequestHandler {

        private final ReverseProxyProperties.Route route;

        public RouteHandler(ReverseProxyProperties.Route route) {
            this.route = route;
        }

        @Override
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
            String uri = parseUriFromRequest(request);
            URI rewriteURI = rewriteUri(uri, route);
            HttpMethod method = HttpMethod.resolve(request.getMethod());
            Assert.notNull(method, String.format("不支持的请求方法:(%s %s)", request.getMethod(), uri));
            log.info("proxy request: {} {} {} upstream to: {}", request.getMethod(), uri, request.getProtocol(), rewriteURI);
            restTemplate.execute(rewriteURI, method, req -> forwardRequest(request, req), res -> forwardResponse(response, res));
        }

        /**
         * 解析访问控制配置
         */
        public CurbAccessConfig resolveAccessConfig(HttpServletRequest request) {
            return CurbReverseProxyHandlerMapping.this.resolveAccessConfig(request, route);
        }

        /**
         * 转发请求到目标上游服务器
         *
         * @param request         原请求对象
         * @param upstreamRequest 上游请求对象
         */
        private void forwardRequest(HttpServletRequest request, ClientHttpRequest upstreamRequest) throws IOException {
            forwardRequestHeaders(request, upstreamRequest);
            try (
                    ServletInputStream is = request.getInputStream();
                    OutputStream os = upstreamRequest.getBody()
            ) {
                StreamUtils.copy(is, os);
            }
        }

        /**
         * 处理转发请求头数据
         *
         * @param request         原请求对象
         * @param upstreamRequest 上游请求对象
         */
        private void forwardRequestHeaders(HttpServletRequest request, ClientHttpRequest upstreamRequest) {
            HttpHeaders upstreamHeaders = upstreamRequest.getHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (ignoreRequestHeader(headerName)) {
                    continue;
                }
                Enumeration<String> headers = request.getHeaders(headerName);
                while (headers.hasMoreElements()) {
                    String headerValue = headers.nextElement();
                    upstreamHeaders.add(headerName, headerValue);
                }
            }
            setXForwardedHeader(request, upstreamHeaders);
            setExtraHeader(route, request, upstreamHeaders);
        }

        /**
         * 设置 X-Forwarded-For 请求头
         *
         * @param request         原请求对象
         * @param upstreamHeaders 上游请求头集合
         */
        private void setXForwardedHeader(HttpServletRequest request, HttpHeaders upstreamHeaders) {
            String ip = ServletUtil.getIp(request);
            if ("127.0.0.1".equals(ip)) {
                return;
            }
            String existingForHeader = request.getHeader(HEADER_X_FORWARDED_FOR);
            if (existingForHeader == null || existingForHeader.isEmpty()) {
                existingForHeader = ip;
            } else {
                existingForHeader = existingForHeader + ", " + ip;
            }
            upstreamHeaders.set(HEADER_X_FORWARDED_FOR, existingForHeader);
        }

        /**
         * 转发响应数据到原请求客户端
         *
         * @param response         原请求的响应对象
         * @param upstreamResponse 上游响应对象
         */
        private Void forwardResponse(HttpServletResponse response, ClientHttpResponse upstreamResponse) throws IOException {
            response.setStatus(upstreamResponse.getRawStatusCode());
            forwardResponseHeaders(response, upstreamResponse);
            try (
                    InputStream source = upstreamResponse.getBody();
                    ServletOutputStream target = response.getOutputStream();
            ) {
                StreamUtils.copy(source, target);
            }
            return null;
        }

        /**
         * 处理转发响应头数据
         *
         * @param response         请求的响应对象
         * @param upstreamResponse 上游响应对象
         */
        private void forwardResponseHeaders(HttpServletResponse response, ClientHttpResponse upstreamResponse) {
            HttpHeaders headers = upstreamResponse.getHeaders();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String headerName = entry.getKey();
                if (ignoreResponseHeader(headerName)) {
                    continue;
                }
                for (String headerValue : entry.getValue()) {
                    response.addHeader(headerName, headerValue);
                }
            }
        }
    }
}
