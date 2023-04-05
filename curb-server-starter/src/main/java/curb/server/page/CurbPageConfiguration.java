package curb.server.page;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.util.UrlPathHelper;

public class CurbPageConfiguration extends DelegatingWebMvcConfiguration {

    @Bean
    public CurbPageRequestHandler amisPageRequestHandler() {
        return new CurbPageRequestHandler();
    }

    @Bean
    public CurbPageRequestHandlerMapping amisPageRequestHandlerMapping(
            CurbPageRequestHandler handler,
            @Qualifier("mvcPathMatcher") PathMatcher pathMatcher,
            @Qualifier("mvcUrlPathHelper") UrlPathHelper urlPathHelper,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        CurbPageRequestHandlerMapping mapping = new CurbPageRequestHandlerMapping(handler);
        mapping.setOrder(1);
        mapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        mapping.setCorsConfigurations(getCorsConfigurations());
        mapping.setPathMatcher(pathMatcher);
        mapping.setUrlPathHelper(urlPathHelper);
//
//        PathMatchConfigurer configurer = getPathMatchConfigurer();
//
//        UrlPathHelper pathHelper = configurer.getUrlPathHelper();
//        if (pathHelper != null) {
//            mapping.setUrlPathHelper(pathHelper);
//        }
//        PathMatcher pathMatcher = configurer.getPathMatcher();
//        if (pathMatcher != null) {
//            mapping.setPathMatcher(pathMatcher);
//        }
        return mapping;
    }

}
