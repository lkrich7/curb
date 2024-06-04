package curb.server.page;

import curb.core.CurbDataProvider;
import curb.server.service.AppMenuService;
import curb.server.service.AppService;
import curb.server.service.PageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.util.UrlPathHelper;

public class CurbPageConfiguration extends DelegatingWebMvcConfiguration {

    @Bean
    public CurbPageRequestHandler amisPageRequestHandler(CurbDataProvider dataProvider,
                                                         PageService pageService,
                                                         AppService appService,
                                                         AppMenuService appMenuService) {
        return new CurbPageRequestHandler(dataProvider, pageService, appService, appMenuService);
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

        return mapping;
    }

}
