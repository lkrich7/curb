package curb.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) return cookie.getValue();
        }
        return null;
    }

    public static void saveCookie(HttpServletResponse response, String key, String value, int second, String path, String domain) {
        domain = stripDomain(domain);
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second, String path, String domain) {
        domain = stripDomain(domain);
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String key, String domain) {
        clearCookie(response, key, 0, "/", domain);
    }

    private static String stripDomain(String domain) {
        int idx = domain.indexOf(':');
        if (idx < 0) {
            return domain;
        }
        return domain.substring(0, idx);
    }

}
