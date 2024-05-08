package curb.core.util;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.PermissionResult;
import curb.core.model.User;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 工具类
 */
public enum CurbUtil {
    ;

    private static final String TOKEN_COOKIE_KEY = "curb-token";

    private static final int TOKEN_COOKIE_EXPIRE = 60 * 60 * 24 * 14;

    private static final String ATTRIBUTE_NAME_GROUP = "curbGroup";

    private static final String ATTRIBUTE_NAME_APP = "curbApp";

    private static final String ATTRIBUTE_NAME_USER = "curbUser";

    private static final String ATTRIBUTE_NAME_PERMISSION_RESULT = "curbPermissionResult";

    public static String getToken(HttpServletRequest request) {
        return ServletUtil.getCookie(request, TOKEN_COOKIE_KEY);
    }

    public static void saveToken(HttpServletResponse response, String token, String domain) {
        ServletUtil.saveCookie(response, TOKEN_COOKIE_KEY, token, TOKEN_COOKIE_EXPIRE, "/", domain);
    }

    public static void clearToken(HttpServletResponse response, String domain) {
        ServletUtil.deleteCookie(response, TOKEN_COOKIE_KEY, domain);
    }

    public static boolean isTokenExpired(Long ts) {
        return Math.abs(System.currentTimeMillis() - ts) > TOKEN_COOKIE_EXPIRE * 1000;
    }

    public static void setGroup(HttpServletRequest request, Group group) {
        request.setAttribute(ATTRIBUTE_NAME_GROUP, group);
    }

    public static Group getGroup(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_GROUP);
    }

    public static Group getGroup(RequestAttributes attributes) {
        return ServletUtil.getObjectFromAttributes(attributes, ATTRIBUTE_NAME_GROUP);
    }

    public static Group getGroup() {
        return ServletUtil.getObjectFromRequestContext(ATTRIBUTE_NAME_GROUP);
    }

    public static void setApp(HttpServletRequest request, App app) {
        request.setAttribute(ATTRIBUTE_NAME_APP, app);
    }

    public static App getApp(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_APP);
    }

    public static App getApp(RequestAttributes attributes) {
        return ServletUtil.getObjectFromAttributes(attributes, ATTRIBUTE_NAME_APP);
    }

    public static App getApp() {
        return ServletUtil.getObjectFromRequestContext(ATTRIBUTE_NAME_APP);
    }

    public static void setUser(HttpServletRequest request, User user) {
        request.setAttribute(ATTRIBUTE_NAME_USER, user);
    }

    public static User getUser(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_USER);
    }

    public static User getUser(RequestAttributes attributes) {
        return ServletUtil.getObjectFromAttributes(attributes, ATTRIBUTE_NAME_USER);
    }

    public static User getUser() {
        return ServletUtil.getObjectFromRequestContext(ATTRIBUTE_NAME_USER);
    }

    public static void setPermissionResult(HttpServletRequest request, PermissionResult permissionResult) {
        request.setAttribute(ATTRIBUTE_NAME_PERMISSION_RESULT, permissionResult);
    }

    public static PermissionResult getPermissionResult(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_PERMISSION_RESULT);
    }

    public static PermissionResult getPermissionResult(RequestAttributes attributes) {
        return ServletUtil.getObjectFromAttributes(attributes, ATTRIBUTE_NAME_PERMISSION_RESULT);
    }

    public static PermissionResult getPermissionResult() {
        return ServletUtil.getObjectFromRequestContext(ATTRIBUTE_NAME_PERMISSION_RESULT);
    }

    public static String getUrl(HttpServletRequest request) {
//        String scheme = getScheme(request);
        String domain = getDomain(request);
        String path = request.getRequestURI();
        String query = StringUtil.trimToNull(request.getQueryString());
        StringBuilder builder = new StringBuilder()
//                .append(scheme).append(":")
                .append("//")
                .append(domain)
                .append(path);
        if (query != null) {
            builder.append("?").append(query);
        }
        return builder.toString();
    }


    /**
     * 返回客户端请求的网络协议名
     *
     * @param request
     * @return
     */
    private static String getScheme(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.trim().isEmpty()) {
            scheme = request.getScheme();
        }
        return scheme.trim().toLowerCase();
    }

    /**
     * 返回客户端请求的域
     *
     * @param request
     * @return
     */
    private static String getDomain(HttpServletRequest request) {
        String domain = request.getHeader("X-CURB-HOST");
        if (StringUtil.isNotBlank(domain)) {
            return domain;
        }
        domain = request.getHeader("Host");
        if (StringUtil.isNotBlank(domain)) {
            return domain;
        }
        int port = request.getServerPort();
        String scheme = getScheme(request);
        domain = buildDomain(request.getServerName(), port, scheme);
        return domain;
    }

    /**
     * 根据主机名、端口号和协议名构造域
     *
     * @param host
     * @param port
     * @param scheme
     * @return
     */
    private static String buildDomain(String host, int port, String scheme) {
        if (("http".equalsIgnoreCase(scheme) && port != 80)
                || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            return host + ":" + port;
        }
        return host;
    }

}
