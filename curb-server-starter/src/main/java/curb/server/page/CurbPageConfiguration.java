package curb.server.page;

import curb.server.configuration.CurbServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.util.UrlPathHelper;

public class CurbPageConfiguration extends DelegatingWebMvcConfiguration {

    @Autowired
    private CurbServerProperties properties;

    @Bean
    public CurbPageRequestHandler amisPageRequestHandler() {
        return new CurbPageRequestHandler();
    }

    @Bean
    public CurbPageRequestHandlerMapping amisPageRequestHandlerMapping(CurbPageRequestHandler handler) {
        CurbPageRequestHandlerMapping mapping = new CurbPageRequestHandlerMapping(handler);
        mapping.setOrder(1);
        mapping.setInterceptors(getInterceptors());
        mapping.setCorsConfigurations(getCorsConfigurations());

        PathMatchConfigurer configurer = getPathMatchConfigurer();

        UrlPathHelper pathHelper = configurer.getUrlPathHelper();
        if (pathHelper != null) {
            mapping.setUrlPathHelper(pathHelper);
        }
        PathMatcher pathMatcher = configurer.getPathMatcher();
        if (pathMatcher != null) {
            mapping.setPathMatcher(pathMatcher);
        }
        return mapping;
    }

    @Bean
    public CurbPageHandlerExceptionResolver amisHandlerExceptionResolver() {
        CurbPageHandlerExceptionResolver ret = new CurbPageHandlerExceptionResolver();
        ret.setOrder(1);
        return ret;

    }
}
