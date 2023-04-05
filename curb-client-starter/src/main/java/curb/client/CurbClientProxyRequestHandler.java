package curb.client;


import curb.core.CurbDataProvider;
import curb.core.model.App;
import curb.core.util.ServletUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.CodingErrorAction;
import java.util.Enumeration;

/**
 * 客户端反向代理转发请求处理器，用于将Curb客户服务中的其余非业务接口请求（系统管理页面等）
 * 转发到Curb服务端，通常用于本地系统本地调试，不建议在生产环境中使用，推荐使用Nginx进行请求路由转发。
 */
public class CurbClientProxyRequestHandler extends AbstractUrlHandlerMapping implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(CurbClientProxyRequestHandler.class);

    @Autowired
    private CurbDataProvider dataProvider;

    private final CloseableHttpClient proxyClient;

    private final HttpHost httpHost;

    private final String[] urlMappings;

    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    private static final HeaderGroup IGNORED_HEADERS;

    static {
        IGNORED_HEADERS = new HeaderGroup();
        String[] headers = new String[]{
                HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.CONNECTION,
                HttpHeaders.PROXY_AUTHENTICATE,
                HttpHeaders.PROXY_AUTHORIZATION,
                HttpHeaders.TRANSFER_ENCODING,
                HttpHeaders.UPGRADE,
                "Keep-Alive",
                "Trailers",
        };
        for (String header : headers) {
            IGNORED_HEADERS.addHeader(new BasicHeader(header, null));
        }
    }

    public CurbClientProxyRequestHandler(String server, String[] urlMappings) {
        proxyClient = createHttpClient();
        httpHost = HttpHost.create(server);
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
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = parseUrlFromRequest(request);
        HttpRequest proxyRequest = newProxyRequest(request, uri);
        copyRequestHeaders(request, proxyRequest);
        setRealIpHeader(request, proxyRequest);

        App app = dataProvider.getApp(request);
        proxyRequest.setHeader("X-CURB-HOST", app.getUrl().getHost());

        log.info("proxy request: {} {} {} upstream: {}", request.getMethod(), uri, request.getProtocol(), httpHost);
        final CloseableHttpResponse proxyResponse = proxyClient.execute(httpHost, proxyRequest);
        try {
            int statusCode = proxyResponse.getStatusLine().getStatusCode();
            response.setStatus(statusCode);
            copyResponseHeaders(proxyResponse, response);
            copyResponseEntity(proxyResponse, response);
        } catch (ClientProtocolException e1) {
            final HttpEntity entity = proxyResponse.getEntity();
            try {
                EntityUtils.consume(entity);
            } catch (Exception e2) {
                log.warn("Error consuming content after an exception. {}", e2.getMessage(), e2);
            }
            throw e1;
        } finally {
            proxyResponse.close();
        }
    }

    private static CloseableHttpClient createHttpClient() {
        SocketConfig defaultSocketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoTimeout(1000)
                .build();

        ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .build();

        PoolingHttpClientConnectionManager defaultConnectionManager = new PoolingHttpClientConnectionManager();
        defaultConnectionManager.setDefaultSocketConfig(defaultSocketConfig);
        defaultConnectionManager.setDefaultConnectionConfig(defaultConnectionConfig);
        defaultConnectionManager.setDefaultMaxPerRoute(100);
        defaultConnectionManager.setMaxTotal(100);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .setConnectTimeout(1000)
                .setSocketTimeout(4000)
                .setRedirectsEnabled(false)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        return HttpClients.custom()
                .setConnectionManager(defaultConnectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setKeepAliveStrategy(WrapDefaultConnectionKeepAliveStrategy.INSTANCE)
                .disableCookieManagement()
                .disableDefaultUserAgent()
                .build();
    }

    private HttpRequest newProxyRequest(HttpServletRequest request, String proxyRequestUri) throws IOException {
        String method = request.getMethod();
        if (request.getHeader(HttpHeaders.CONTENT_LENGTH) != null ||
                request.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            //spec: RFC 2616, sec 4.3: either of these two headers signal that there is a message body.
            HttpEntityEnclosingRequest proxyRequest = new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
            // Add the input entity (streamed)
            // note: we don't bother ensuring we close the servletInputStream since the container handles it
            proxyRequest.setEntity(new InputStreamEntity(request.getInputStream(), getContentLength(request)));
            return proxyRequest;
        } else {
            return new BasicHttpRequest(method, proxyRequestUri);
        }
    }

    private String parseUrlFromRequest(HttpServletRequest request) {
        StringBuilder uri = new StringBuilder(500);
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        uri.append(path);
        if (queryString != null && !queryString.isEmpty()) {
            uri.append('?');
            uri.append(queryString);
        }
        return uri.toString();
    }

    private long getContentLength(HttpServletRequest request) {
        String contentLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthHeader != null) {
            return Long.parseLong(contentLengthHeader);
        }
        return -1L;
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpRequest proxyRequest) {
        Enumeration<String> enumerationOfHeaderNames = request.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            if (IGNORED_HEADERS.containsHeader(headerName)
                    || headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                continue;
            }
            if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                String headerValue = httpHost.toHostString();
                proxyRequest.addHeader(headerName, headerValue);
            } else {
                Enumeration<String> headers = request.getHeaders(headerName);
                while (headers.hasMoreElements()) {
                    String headerValue = headers.nextElement();
                    proxyRequest.addHeader(headerName, headerValue);
                }
            }
        }
    }

    private void setRealIpHeader(HttpServletRequest request, HttpRequest proxyRequest) {
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
        proxyRequest.setHeader(xForwardedFor, existingForHeader);
        proxyRequest.setHeader("X-Real-IP", ip);
    }

    private void copyResponseHeaders(HttpResponse proxyResponse, HttpServletResponse response) {
        for (Header header : proxyResponse.getAllHeaders()) {
            String headerName = header.getName();
            if (IGNORED_HEADERS.containsHeader(headerName)) {
                continue;
            }
            String headerValue = header.getValue();
            response.addHeader(headerName, headerValue);
        }
    }

    private void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse response) throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = response.getOutputStream();
            entity.writeTo(servletOutputStream);
        }
    }

}
