package curb.core.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 与HttpServletRequest/HttpServletResponse 相关的工具类
 */
public enum ServletUtil {
    ;

    private static final String UNKNOWN = "unknown";

    /**
     * 获取请求客户端真实IP
     *
     * @param request request
     * @return 请求客户端真实IP null说明无法取得
     */
    public static String getIp(HttpServletRequest request) {
        String ip = getIpRaw(request);
        if (ip == null) {
            return null;
        }
        int idx = ip.indexOf(',');
        if (idx > 0) {
            // 会出现多个IP 取第一个
            return ip.substring(0, idx);
        }
        return ip;
    }

    private static String getIpRaw(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getRemoteAddr();
        if (ip != null && !ip.isEmpty()) {
            return ip;
        }
        return null;
    }

    /**
     * 返回客户端请求的网络协议名
     *
     * @param request 请求对象
     * @return 协议名
     */
    public static String getScheme(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        scheme = StringUtil.trimToEmpty(scheme);
        if (scheme.isEmpty()) {
            scheme = StringUtil.trimToEmpty(request.getScheme());
        }
        return scheme.toLowerCase();
    }

    /**
     * 返回客户端请求的域名
     *
     * @param request 请求对象
     * @return 请求的域名
     */
    public static String getDomain(HttpServletRequest request) {
        String domain = request.getHeader("Host");
        if (StringUtil.isNotBlank(domain)) {
            return domain;
        }
        int port = request.getServerPort();
        String scheme = ServletUtil.getScheme(request);
        domain = buildDomain(request.getServerName(), port, scheme);
        return domain;
    }

    private static String buildDomain(String host, int port, String scheme) {
        if (("http".equalsIgnoreCase(scheme) && port != 80)
                || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            return host + ":" + port;
        }
        return host;
    }

    /**
     * 取得请求的URL 路径和参数
     *
     * @param request 请求对象
     * @return 请求的URL路径和参数
     */
    public static String getUrlPathAndQuery(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            path += "?" + query;
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectFromRequest(HttpServletRequest request, String name) {
        Object obj = request.getAttribute(name);
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectFromAttributes(RequestAttributes attributes, String name) {
        Object obj = attributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
        return (T) obj;
    }

    public static <T> T getObjectFromRequestContext(String name) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        return getObjectFromAttributes(requestAttributes, name);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        setCookie(response, name, value, maxAge, "/", null, false);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path, String domain, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        if (StringUtil.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key) {
        clearCookie(response, key, null, null);
    }

    public static void clearCookie(HttpServletResponse response, String key, String path, String domain) {
        setCookie(response, key, null, 0, path, domain, false);
    }

}
