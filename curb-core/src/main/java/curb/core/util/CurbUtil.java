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
public final class CurbUtil {

    private static final String TOKEN_COOKIE_KEY = "curb-token";

    private static final int TOKEN_COOKIE_EXPIRE = 60 * 60 * 24 * 14;

    private static final String ATTRIBUTE_NAME_GROUP = "curbGroup";

    private static final String ATTRIBUTE_NAME_APP = "curbApp";

    private static final String ATTRIBUTE_NAME_USER = "curbUser";

    private static final String ATTRIBUTE_NAME_PERMISSION_RESULT = "curbPermissionResult";

    private CurbUtil() {
    }

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

}
