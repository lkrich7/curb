package curb.core.configuration;

import javax.servlet.DispatcherType;
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
    private Set<DispatcherType> includeDispatcherTypes;

    /**
     * 是否排除对静态资源请求的检查
     */
    private boolean excludeStaticResource = false;

    /**
     * 测试模式设置
     */
    private final TestMode testMode = new TestMode();

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

    public TestMode getTestMode() {
        return testMode;
    }

}
