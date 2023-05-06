package curb.client;


import curb.core.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 客户端反向代理转发请求处理器，用于将Curb客户服务中的其余非业务接口请求（系统管理页面等）
 * 转发到Curb服务端，通常用于本地系统本地调试，不建议在生产环境中使用，推荐使用Nginx进行请求路由转发。
 */
public class CurbClientProxyRequestHandler extends AbstractUrlHandlerMapping implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(CurbClientProxyRequestHandler.class);
    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     */
    private static final Set<String> IGNORED_HEADERS;

    static {
        IGNORED_HEADERS = new HashSet<>();
        String[] headers = new String[]{
                HttpHeaders.CONNECTION,
                HttpHeaders.PROXY_AUTHENTICATE,
                HttpHeaders.PROXY_AUTHORIZATION,
                HttpHeaders.TRANSFER_ENCODING,
                HttpHeaders.UPGRADE,
                "Keep-Alive",
                "Trailers",
        };
        for (String header : headers) {
            IGNORED_HEADERS.add(header.toLowerCase());
        }
    }

    private final RestTemplate restTemplate;
    private final String server;
    private final String[] urlMappings;

    public CurbClientProxyRequestHandler(RestTemplate restTemplate, String server, String[] urlMappings) {
        this.restTemplate = restTemplate;
        this.server = server;
        this.urlMappings = urlMappings;

        //默认设设置顺序排在静态资源之后
        setOrder(Ordered.LOWEST_PRECEDENCE);
    }

    @Override
    public void initApplicationContext() {
        super.initApplicationContext();
        if (urlMappings != null) {
            for (String url : urlMappings) {
                registerHandler(url, this);
            }
        }
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        String uri = parseUriFromRequest(request);
        String url = server + uri;
        log.info("proxy request: {} {} {} upstream to: {}", request.getMethod(), uri, request.getProtocol(), url);
        restTemplate.execute(url, method, req -> forwardRequest(request, req), res -> forwardResponse(response, res));
    }

    private String parseUriFromRequest(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isEmpty()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }

    private void forwardRequest(HttpServletRequest request, ClientHttpRequest upstreamRequest) throws IOException {
        copyRequestHeaders(request, upstreamRequest);
        try (
                ServletInputStream is = request.getInputStream();
                OutputStream os = upstreamRequest.getBody()
        ) {
            StreamUtils.copy(is, os);
        }
    }

    private void copyRequestHeaders(HttpServletRequest request, ClientHttpRequest upstreamRequest) {
        HttpHeaders upstreamHeaders = upstreamRequest.getHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (IGNORED_HEADERS.contains(headerName.toLowerCase())) {
                continue;
            }
            if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                continue;
            }
            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                upstreamHeaders.add(headerName, headerValue);
            }
        }
        setRealIpHeader(request, upstreamHeaders);
    }

    private void setRealIpHeader(HttpServletRequest request, HttpHeaders upstreamHeaders) {
        String ip = ServletUtil.getIp(request);
        if ("127.0.0.1".equals(ip)) {
            return;
        }
        String xForwardedFor = "X-Forwarded-For";
        String existingForHeader = request.getHeader(xForwardedFor);
        if (existingForHeader == null || existingForHeader.isEmpty()) {
            existingForHeader = ip;
        } else {
            existingForHeader = existingForHeader + ", " + ip;
        }
        upstreamHeaders.set(xForwardedFor, existingForHeader);
    }

    private Void forwardResponse(HttpServletResponse response, ClientHttpResponse clientHttpResponse) throws IOException {
        response.setStatus(clientHttpResponse.getRawStatusCode());
        HttpHeaders headers = clientHttpResponse.getHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (String headerValue : entry.getValue()) {
                response.addHeader(headerName, headerValue);
            }
        }
        try (
                InputStream source = clientHttpResponse.getBody();
                ServletOutputStream target = response.getOutputStream();
        ) {
            StreamUtils.copy(source, target);
        }

        return null;
    }


}
