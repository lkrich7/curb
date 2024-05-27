package curb.client.configuration;

import curb.client.CurbApi;
import curb.client.CurbApiRestClient;
import curb.client.CurbClientDataProvider;
import curb.core.CurbDataProvider;
import curb.core.DefaultPermissionResolver;
import curb.core.PermissionResolver;
import curb.core.configuration.ReverseProxyProperties;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.core.mvc.proxy.CurbReverseProxyHandlerMapping;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Curb 配置
 */
public class CurbClientConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "defaultPermissionResolver")
    public PermissionResolver defaultPermissionResolver() {
        return new DefaultPermissionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "curbClientRestTemplate")
    public RestTemplate curbClientRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(CurbApi.class)
    public CurbApi curbApiRestClient(@Qualifier("curbClientRestTemplate") RestTemplate curbClientRestTemplate,
                                     CurbClientProperties properties) {
        CurbClientProperties.Client client = properties.getClient();
        return new CurbApiRestClient(curbClientRestTemplate, client.getServer(), client.getAppid(), client.getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(CurbDataProvider.class)
    public CurbDataProvider curbDataProvider(CurbApi curbClient) {
        return new CurbClientDataProvider(curbClient);
    }

    @Bean
    @ConditionalOnMissingBean(CurbInterceptor.class)
    public CurbInterceptor curbInterceptor(CurbDataProvider dataProvider,
                                           PermissionResolver defaultPermissionResolver,
                                           CurbClientProperties properties) {
        return new CurbInterceptor(dataProvider, defaultPermissionResolver, properties);
    }

    @Bean
    @ConditionalOnProperty(name = "curb.reverse-proxy.enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean(CurbReverseProxyHandlerMapping.class)
    public CurbReverseProxyHandlerMapping curbReverseProxyRequestHandler(
            CurbClientProperties properties,
            RestTemplate curbClientRestTemplate) {
        ReverseProxyProperties reverseProxy = properties.getReverseProxy();

        List<ReverseProxyProperties.Route> routes = reverseProxy.getRoutes();
        if (routes == null || routes.isEmpty()) {
            // 缺省时添加默认路由 转发到curb server
            ReverseProxyProperties.Route defaultRoute = new ReverseProxyProperties.Route();
            defaultRoute.setServer(properties.getClient().getServer());
            defaultRoute.setPathPatterns(new ArrayList<>(Collections.singletonList("/**")));
            routes = new ArrayList<>();
            routes.add(defaultRoute);
            reverseProxy.setRoutes(routes);
        }
        return new CurbReverseProxyHandlerMapping(reverseProxy, curbClientRestTemplate);
    }

}
