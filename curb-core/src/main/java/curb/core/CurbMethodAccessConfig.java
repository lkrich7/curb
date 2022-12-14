package curb.core;

import curb.core.annotation.CurbMethod;

import java.lang.reflect.Method;

public class CurbMethodAccessConfig implements CurbAccessConfig {

    private static final CurbMethodAccessConfig DEFAULT = defaultCurbMethodAccessConfig();

    private final CurbMethod annotation;

    public CurbMethodAccessConfig() {
        this.annotation = DEFAULT.annotation;
    }

    public CurbMethodAccessConfig(CurbMethod annotation) {
        this.annotation = annotation;
    }

    /**
     * 该方法用于初始化默认请求配置
     *
     * @return
     */
    @CurbMethod
    private static CurbMethodAccessConfig defaultCurbMethodAccessConfig() {
        try {
            String name = Thread.currentThread().getStackTrace()[1].getMethodName();
            Method method = CurbMethodAccessConfig.class.getDeclaredMethod(name);
            CurbMethod annotation = method.getAnnotation(CurbMethod.class);
            return new CurbMethodAccessConfig(annotation);
        } catch (NoSuchMethodException e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    @Override
    public AccessLevel getLevel() {
        return annotation.level();
    }

    @Override
    public String getSign() {
        return annotation.sign();
    }

    @Override
    public String getPath() {
        return annotation.path();
    }

    @Override
    public boolean isCheckParam() {
        return annotation.checkParam();
    }

    @Override
    public String[] getIgnoreParamNames() {
        return annotation.ignoreParamNames();
    }

    @Override
    public Class<? extends PermissionResolver> getResolverClass() {
        return annotation.resolver();
    }
}
