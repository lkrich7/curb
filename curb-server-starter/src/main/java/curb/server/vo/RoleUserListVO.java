package curb.server.vo;

import curb.core.model.User;

import java.io.Serializable;
import java.util.Collection;

/**
 * 角色-用户 列表
 */
public class RoleUserListVO implements Serializable {
    private int roleId;
    private Collection<User> userId;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Collection<User> getUserId() {
        return userId;
    }

    public void setUserId(Collection<User> userId) {
        this.userId = userId;
    }
}
