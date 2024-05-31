package curb.core;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.PermissionResult;
import curb.core.model.User;
import curb.core.model.UserState;

/**
 * 请求上下文
 */
public class CurbRequestContext {
    private String method;
    private String url;
    private App app;
    private Group group;
    private User user;
    private CurbAccessConfig accessConfig;
    private PermissionResult permissionResult;
    private AccessState accessState;
    private boolean testMode;
    private long startTime;

    /**
     * 计算请求总耗时
     */
    public long totalTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 检查用户账号状体
     *
     * @return 用户账号状态
     */
    public UserState userState() {
        return UserState.check(user);
    }

    /**
     * 判断是否通过身份认证
     *
     * @return 是否通过身份认证
     */
    public boolean isAuthenticated() {
        return userState().isOk();
    }

    /**
     * 判断是否通过权限检查
     *
     * @return 是否通过权限检查
     */
    public boolean isAuthorized() {
        return permissionResult != null && permissionResult.isAuthorized();
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public AccessState getAccessState() {
        return accessState;
    }

    public void setAccessState(AccessState accessState) {
        this.accessState = accessState;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurbAccessConfig getAccessConfig() {
        return accessConfig;
    }

    public void setAccessConfig(CurbAccessConfig accessConfig) {
        this.accessConfig = accessConfig;
    }

    public PermissionResult getPermissionResult() {
        return permissionResult;
    }

    public void setPermissionResult(PermissionResult permissionResult) {
        this.permissionResult = permissionResult;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "CurbRequestContext{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", app=" + app +
                ", group=" + group +
                ", user=" + user +
                ", accessConfig=" + accessConfig +
                ", permissionResult=" + permissionResult +
                ", accessState=" + accessState +
                ", testMode=" + testMode +
                ", startTime=" + startTime +
                '}';
    }
}
