package curb.client.configuration;

import curb.client.CurbApiRestClient;
import curb.client.CurbClientDataProvider;
import curb.client.CurbApi;
import curb.client.CurbClientProxyRequestHandler;
import curb.core.CurbDataProvider;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.core.DefaultPermissionResolver;
import curb.core.PermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Curb 配置
 */
public class CurbClientConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "defaultRequestPermissionParser")
    public PermissionResolver defaultRequestPermissionParser() {
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
    public CurbDataProvider curbDataProvider(@Autowired CurbApi curbClient) {
        return new CurbClientDataProvider(curbClient);
    }

    @Bean
    @ConditionalOnMissingBean(CurbInterceptor.class)
    public CurbInterceptor curbInterceptor(@Autowired CurbClientProperties properties) {
        return new CurbInterceptor(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "curb.reverse-proxy.enabled", havingValue = "true", matchIfMissing = false)
    public CurbClientProxyRequestHandler curbReverseProxyRequestHandler(
            @Qualifier("curbClientRestTemplate") RestTemplate curbClientRestTemplate,
            CurbClientProperties properties) {
        String server = properties.getClient().getServer();
        String[] urlMappings = properties.getReverseProxy().getUrlMappings();
        return new CurbClientProxyRequestHandler(curbClientRestTemplate, server, urlMappings);
    }

}
