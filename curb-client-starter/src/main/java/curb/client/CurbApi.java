package curb.client;


import curb.core.ApiResult;
import curb.core.model.AppDetail;
import curb.core.model.User;
import curb.core.model.UserPermission;

import java.util.List;

/**
 * Curb API 接口
 */
public interface CurbApi {

    /**
     * 应用详细数据
     *
     * @return
     */
    ApiResult<AppDetail> getAppDetail();

    /**
     * 根据用户登录态token获取用户信息
     *
     * @param token
     * @return
     */
    ApiResult<User> getUser(String token);

    /**
     * 获取用户的权限信息
     *
     * @param userId
     * @return
     */
    ApiResult<List<UserPermission>> listUserPermissions(Integer userId);

}
