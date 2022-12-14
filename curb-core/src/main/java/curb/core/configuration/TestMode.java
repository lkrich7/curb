package curb.core.configuration;

import curb.core.model.User;

/**
 * 测试模式设置
 */
public class TestMode {

    /**
     * 是否启用测试模式
     */
    private boolean enabled = false;

    /**
     * 测试用户配置
     */
    private User user;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TestMode{" +
                "enabled=" + enabled +
                ", user=" + user +
                '}';
    }
}
