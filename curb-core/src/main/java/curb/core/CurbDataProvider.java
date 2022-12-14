package curb.core;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;

import javax.servlet.http.HttpServletRequest;

/**
 * Curb 数据提供者接口
 */
public interface CurbDataProvider {

    /**
     * 根据用户请求获取应用信息
     *
     * @param request
     * @return
     */
    App getApp(HttpServletRequest request);

    /**
     * 根据用户请求获取项目组信息
     *
     * @param request
     * @return
     */
    Group getGroup(HttpServletRequest request);

    /**
     * 根据用户请求获取用户信息
     *
     * @param request
     * @return
     */
    User getUser(HttpServletRequest request);

    /**
     * 获取用户的应用权限
     *
     * @param user
     * @param app
     * @param group
     * @return
     */
    UserAppPermissions getUserAppPermissions(User user, App app, Group group);

}
