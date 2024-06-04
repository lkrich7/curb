package curb.core.configuration;

import curb.core.AccessLevel;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 简易反向代理配置
 */
public class ReverseProxyProperties {

    /**
     * 是否启用反向代理
     */
    private boolean enabled = false;

    /**
     * Bean的加载顺序
     */
    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * 访问控制级别
     */
    private AccessLevel accessLevel = AccessLevel.ANONYMOUS;

    /**
     * 不转发的请求头
     */
    private List<String> ignoredRequestHeaders = defaultIgnoredHeaders();

    /**
     * 不转发的响应头
     */
    private List<String> ignoredResponseHeaders = defaultIgnoredHeaders();

    /**
     * 反向代理路径公共前缀
     */
    private String prefix;

    /**
     * 反向代理路由配置
     */
    private List<Route> routes;

    private static List<String> defaultIgnoredHeaders() {
        return Arrays.stream(new String[]{
                HttpHeaders.CONNECTION,
                HttpHeaders.PROXY_AUTHENTICATE,
                HttpHeaders.PROXY_AUTHORIZATION,
                HttpHeaders.TRANSFER_ENCODING,
                HttpHeaders.UPGRADE,
                HttpHeaders.HOST,
                "Keep-Alive",
                "Trailers",
        }).collect(Collectors.toList());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<String> getIgnoredRequestHeaders() {
        return ignoredRequestHeaders;
    }

    public void setIgnoredRequestHeaders(List<String> ignoredRequestHeaders) {
        this.ignoredRequestHeaders = ignoredRequestHeaders;
    }

    public List<String> getIgnoredResponseHeaders() {
        return ignoredResponseHeaders;
    }

    public void setIgnoredResponseHeaders(List<String> ignoredResponseHeaders) {
        this.ignoredResponseHeaders = ignoredResponseHeaders;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Set<String> ignoredRequestHeadersSet() {
        return initHeaderSet(ignoredRequestHeaders);
    }

    public Set<String> ignoredResponseHeadersSet() {
        return initHeaderSet(ignoredResponseHeaders);
    }

    private Set<String> initHeaderSet(Collection<String> headers) {
        if (headers == null) {
            return Collections.emptySet();
        }
        return headers.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    /**
     * 反向代理目标路由配置
     */
    public static class Route {

        /**
         * 路由名称
         */
        private String name;

        /**
         * 代理目标服务器地址
         */
        private String server;

        /**
         * 代理路径Pattern (AntPath)
         */
        private List<String> pathPatterns;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public List<String> getPathPatterns() {
            return pathPatterns;
        }

        public void setPathPatterns(List<String> pathPatterns) {
            this.pathPatterns = pathPatterns;
        }

        @Override
        public String toString() {
            return "Route{" +
                    "name='" + name + '\'' +
                    ", server='" + server + '\'' +
                    ", pathPatterns=" + pathPatterns +
                    '}';
        }
    }
}
