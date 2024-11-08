package curb.core.configuration;

import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.model.UserState;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Set;

/**
 * Curb的通用配置属性
 */
public class CurbProperties {

    /**
     * 是否启用Curb
     */
    private boolean enabled = true;
    /**
     * Curb 拦截器包含的路径Pattern (AntPath)
     */
    private String[] includePathPatterns = new String[]{"/**"};
    /**
     * Curb 拦截器排除的路径Pattern (AntPath)
     */
    private String[] excludePathPatterns;
    /**
     * 跨域路径Pattern (AndPath)
     */
    private String[] corsPathPatterns;
    /**
     * 参与执行检查的请求的DispatcherType集合
     */
    private Set<DispatcherType> includeDispatcherTypes = EnumSet.of(DispatcherType.REQUEST);
    /**
     * 是否排除对静态资源请求的检查
     */
    private boolean excludeStaticResource = false;

    /**
     * 保存token的cookie字段名
     */
    private String tokenName = "curb-token";

    /**
     * 反向代理配置
     */
    private ReverseProxyProperties reverseProxy = new ReverseProxyProperties();

    /**
     * 测试模式设置
     */
    private TestModeProperties testMode = new TestModeProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getIncludePathPatterns() {
        return includePathPatterns;
    }

    public void setIncludePathPatterns(String[] includePathPatterns) {
        this.includePathPatterns = includePathPatterns;
    }

    public String[] getExcludePathPatterns() {
        return excludePathPatterns;
    }

    public void setExcludePathPatterns(String[] excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }

    public String[] getCorsPathPatterns() {
        return corsPathPatterns;
    }

    public void setCorsPathPatterns(String[] corsPathPatterns) {
        this.corsPathPatterns = corsPathPatterns;
    }

    public Set<DispatcherType> getIncludeDispatcherTypes() {
        return includeDispatcherTypes;
    }

    public void setIncludeDispatcherTypes(Set<DispatcherType> includeDispatcherTypes) {
        this.includeDispatcherTypes = includeDispatcherTypes;
    }

    public boolean isExcludeStaticResource() {
        return excludeStaticResource;
    }

    public void setExcludeStaticResource(boolean excludeStaticResource) {
        this.excludeStaticResource = excludeStaticResource;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public ReverseProxyProperties getReverseProxy() {
        return reverseProxy;
    }

    public void setReverseProxy(ReverseProxyProperties reverseProxy) {
        this.reverseProxy = reverseProxy;
    }

    public TestModeProperties getTestMode() {
        return testMode;
    }

    public void setTestMode(TestModeProperties testMode) {
        this.testMode = testMode;
    }

    public User testUser() {
        if (inTestMode()) {
            User testUser = testMode.getUser();
            if (testUser != null && testUser.getState() == null) {
                testUser.setState(UserState.OK.getCode());
            }
            return testUser;
        }
        return null;
    }

    public UserAppPermissions testUserAppPermissions() {
        if (inTestMode()) {
            return testMode.getUserAppPermissions();
        }
        return null;
    }

    public boolean inTestMode() {
        return testMode != null && testMode.isEnabled();
    }

    /**
     * 是否排除DispatcherType
     */
    public boolean excludeDispatcherType(DispatcherType dispatcherType) {
        Set<DispatcherType> types = this.includeDispatcherTypes;
        if (types == null || types.isEmpty()) {
            types = EnumSet.of(DispatcherType.REQUEST);
        }
        return !types.contains(dispatcherType);
    }

}
