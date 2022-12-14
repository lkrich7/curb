package curb.core;


/**
 * Curb 访问控制配置接口
 */
public interface CurbAccessConfig {

    /**
     * 访问控制级别
     *
     * @return
     */
    AccessLevel getLevel();

    /**
     * 使用的权限标识，如果设置该值，则跳过解析器解析，直接使用该标识匹配
     *
     * @return
     */
    String getSign();

    /**
     * 使用的权限路径，如果设置该值，则在解析请求时使用该路径进行替换
     *
     * @return
     */
    String getPath();

    /**
     * 权限检查是参数
     *
     * @return
     */
    default boolean isCheckParam() {
        return false;
    }

    /**
     * 不参与权限检查的参数名
     *
     * @return
     */
    default String[] getIgnoreParamNames() {
        return null;
    }

    /**
     * 权限解析器类 缺省将使用拦截器设置的默认解析器
     *
     * @return
     */
    default Class<? extends PermissionResolver> getResolverClass() {
        return null;
    }

}
