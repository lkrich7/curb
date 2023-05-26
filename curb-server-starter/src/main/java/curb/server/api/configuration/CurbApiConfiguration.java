package curb.server.api.configuration;

import curb.server.api.interceptor.CurbApiInterceptor;
import curb.server.service.AppService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CurbApiConfiguration {
    @Bean
    public CurbApiInterceptor curbApiInterceptor(AppService appService) {
        return new CurbApiInterceptor(appService);
    }

    @Bean
    public CurbApiWebMvcConfigurer curbApiWebMvcConfigurer(CurbApiInterceptor curbApiInterceptor) {
        return new CurbApiWebMvcConfigurer(curbApiInterceptor);
    }

    static class CurbApiWebMvcConfigurer implements WebMvcConfigurer {

        private final CurbApiInterceptor curbApiInterceptor;

        public CurbApiWebMvcConfigurer(CurbApiInterceptor curbApiInterceptor) {
            this.curbApiInterceptor = curbApiInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(curbApiInterceptor)
                    .addPathPatterns("/api/**");
        }
    }


}
