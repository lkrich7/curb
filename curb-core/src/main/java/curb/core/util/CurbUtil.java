package curb.core.util;

import curb.core.CurbRequestContext;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.PermissionResult;
import curb.core.model.User;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 工具类
 */
public enum CurbUtil {
    ;

    private static final String ATTRIBUTE_NAME_CONTEXT = "curbContext";

    public static CurbRequestContext createContext(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        CurbRequestContext context = new CurbRequestContext();
        context.setStartTime(startTime);
        context.setIp(ServletUtil.getIp(request));
        context.setMethod(request.getMethod());
        context.setUrl(getUrl(request));
        setContext(request, context);
        return context;
    }

    public static void setContext(HttpServletRequest request, CurbRequestContext context) {
        request.setAttribute(ATTRIBUTE_NAME_CONTEXT, context);
    }

    public static CurbRequestContext getContext(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_CONTEXT);
    }

    public static CurbRequestContext getContext(RequestAttributes attributes) {
        return ServletUtil.getObjectFromAttributes(attributes, ATTRIBUTE_NAME_CONTEXT);
    }

    public static CurbRequestContext getContext() {
        return ServletUtil.getObjectFromRequestContext(ATTRIBUTE_NAME_CONTEXT);
    }

    public static Group getGroup(HttpServletRequest request) {
        CurbRequestContext context = getContext(request);
        return context == null ? null : context.getGroup();
    }

    public static Group getGroup(RequestAttributes attributes) {
        CurbRequestContext context = getContext(attributes);
        return context == null ? null : context.getGroup();
    }

    public static Group getGroup() {
        CurbRequestContext context = getContext();
        return context == null ? null : context.getGroup();
    }

    public static App getApp(HttpServletRequest request) {
        CurbRequestContext context = getContext(request);
        return context == null ? null : context.getApp();
    }

    public static App getApp(RequestAttributes attributes) {
        CurbRequestContext context = getContext(attributes);
        return context == null ? null : context.getApp();
    }

    public static App getApp() {
        CurbRequestContext context = getContext();
        return context == null ? null : context.getApp();
    }

    public static User getUser(HttpServletRequest request) {
        CurbRequestContext context = getContext(request);
        return context == null ? null : context.getUser();
    }

    public static User getUser(RequestAttributes attributes) {
        CurbRequestContext context = getContext(attributes);
        return context == null ? null : context.getUser();
    }

    public static User getUser() {
        CurbRequestContext context = getContext();
        return context == null ? null : context.getUser();
    }

    public static PermissionResult getPermissionResult(HttpServletRequest request) {
        CurbRequestContext context = getContext(request);
        return context == null ? null : context.getPermissionResult();
    }

    public static PermissionResult getPermissionResult(RequestAttributes attributes) {
        CurbRequestContext context = getContext(attributes);
        return context == null ? null : context.getPermissionResult();
    }

    public static PermissionResult getPermissionResult() {
        CurbRequestContext context = getContext();
        return context == null ? null : context.getPermissionResult();
    }

    /**
     * 返回客户端请求的URL(不带协议
     *
     * @param request 请求对象
     * @return 完整URL
     */
    public static String getUrl(HttpServletRequest request) {
        String domain = getDomain(request);
        String pathAndQuery = ServletUtil.getUrlPathAndQuery(request);
        return "//" + domain + pathAndQuery;  // 为了同时兼容http和https，返回的url不带协议名
    }

    /**
     * 返回客户端请求的域
     *
     * @param request 请求对象
     * @return 客户端请求的域
     */
    public static String getDomain(HttpServletRequest request) {
        String domain = request.getHeader("X-CURB-HOST");
        if (StringUtil.isNotBlank(domain)) {
            return domain;
        }
        return ServletUtil.getDomain(request);
    }
}
