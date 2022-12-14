package curb.core;

import curb.core.model.Permission;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求权限对象解析器接口
 */
public interface PermissionResolver {

    /**
     * 从用户请求中解析权限对象
     *
     * @param request 用户请求
     * @param config  当前请求的权限配置数据
     * @param handler 拦截器中的HandlerMethod对象
     * @return
     */
    Permission resolve(HttpServletRequest request, CurbAccessConfig config, Object handler);

}
