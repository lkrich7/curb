package curb.server.api.configuration;

import curb.server.api.interceptor.CurbApiInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CurbApiConfiguration implements WebMvcConfigurer {

    @Bean
    public CurbApiInterceptor apiTokenInterceptor() {
        return new CurbApiInterceptor();
    }

    @Autowired
    private CurbApiInterceptor apiTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(apiTokenInterceptor)
                .addPathPatterns("/api/**");

    }
}
