package curb.core.configuration;

import curb.core.model.User;
import curb.core.model.UserAppPermissions;

import java.util.List;

/**
 * 测试模式设置
 */
public class TestModeProperties {

    /**
     * 是否启用测试模式
     */
    private boolean enabled = false;

    /**
     * 测试用户配置
     */
    private User user;

    /**
     * 测试用户拥有的权限
     */
    private List<String> permissions;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public UserAppPermissions getUserAppPermissions() {
        if (permissions == null || permissions.isEmpty()) {
            return null;
        }
        return UserAppPermissions.buildWithSigns(permissions);
    }

    @Override
    public String toString() {
        return "TestModeProperties{" +
                "enabled=" + enabled +
                ", user=" + user +
                ", permissions=" + permissions +
                '}';
    }
}
