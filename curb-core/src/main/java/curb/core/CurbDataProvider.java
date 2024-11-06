package curb.core;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;

/**
 * Curb 数据提供者接口
 */
public interface CurbDataProvider {

    /**
     * 根据请求URL查询应用信息
     *
     * @param url 当前请求
     * @return 应用对象
     */
    App getApp(String url);

    /**
     * 根据请求上下文查询项目组信息
     *
     * @param context 当前请求上下文对象
     * @return 应用对象
     */
    Group getGroup(CurbRequestContext context);

    /**
     * 根据请求上下文 解析token，查询当前用户信息
     *
     * @param encryptedToken 待解析token
     * @param context        当前请求上下文对象
     * @return 用户对象
     */
    User getUser(String encryptedToken, CurbRequestContext context);

    /**
     * 根据请求上下文 获取用户的应用权限
     *
     * @param context 当前请求上下文对象
     * @return 用户应用权限对象
     */
    UserAppPermissions getUserAppPermissions(CurbRequestContext context);

    /**
     * 记录请求
     *
     * @param context 当前请求上下文对象
     */
    void recordRequest(CurbRequestContext context);
}
