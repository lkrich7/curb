package curb.core.annotation;

import curb.core.AccessLevel;
import curb.core.PermissionResolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限控制方法注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurbMethod {

    /**
     * 访问控制级别
     *
     * @return
     */
    AccessLevel level() default AccessLevel.PERMISSION;

    /**
     * 使用的权限标识，如果设置该值，则跳过解析器解析，直接使用该标识匹配权限
     *
     * @return
     */
    String sign() default "";

    /**
     * 权限解析器类 使用拦截器设置的默认解析器
     *
     * @return
     */
    Class<? extends PermissionResolver> resolver() default PermissionResolver.class;

    /**
     * 使用的权限路径，如果设置该值，则在解析请求时使用该路径进行替换
     *
     * @return
     */
    String path() default "";

    /**
     * 权限检查参数
     *
     * @return
     */
    boolean checkParam() default false;

    /**
     * 不参与权限检查的参数名
     *
     * @return
     */
    String[] ignoreParamNames() default {};


}
