package curb.core.mvc;

import curb.core.configuration.CurbProperties;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.core.mvc.resolver.CurbAppArgumentResolver;
import curb.core.mvc.resolver.CurbGroupArgumentResolver;
import curb.core.mvc.resolver.CurbHandlerExceptionResolver;
import curb.core.mvc.resolver.CurbPermissionResultArgumentResolver;
import curb.core.mvc.resolver.CurbUserArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Curb 的 WebMVC 配置类
 */
public class CurbWebMvcConfigurer implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbWebMvcConfigurer.class);

    private final CurbInterceptor curbInterceptor;

    private final CurbProperties curbProperties;

    public CurbWebMvcConfigurer(CurbInterceptor curbInterceptor, CurbProperties curbProperties) {
        this.curbInterceptor = curbInterceptor;
        this.curbProperties = curbProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] includePath = curbProperties.getIncludePathPatterns();
        String[] excludePath = curbProperties.getExcludePathPatterns();

        InterceptorRegistration registration = registry.addInterceptor(curbInterceptor);
        if (includePath != null) {
            registration.addPathPatterns(includePath);
        }
        if (excludePath != null) {
            registration.excludePathPatterns(excludePath);
        }

        LOGGER.info("curb interceptor registered: {}, includePath={}, excludePath={}", curbInterceptor, includePath, excludePath);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CurbUserArgumentResolver());
        argumentResolvers.add(new CurbGroupArgumentResolver());
        argumentResolvers.add(new CurbAppArgumentResolver());
        argumentResolvers.add(new CurbPermissionResultArgumentResolver());
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        CurbHandlerExceptionResolver resolver = new CurbHandlerExceptionResolver();
        resolver.setOrder(1);
        resolvers.add(resolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] pathPatterns = curbProperties.getCorsPathPatterns();
        if (pathPatterns == null) {
            return;
        }
        for (String pathPattern : pathPatterns) {
            registry.addMapping(pathPattern);
        }
    }

}
