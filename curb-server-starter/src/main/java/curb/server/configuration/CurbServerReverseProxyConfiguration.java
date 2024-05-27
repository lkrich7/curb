package curb.server.configuration;

import curb.core.configuration.ReverseProxyProperties;
import curb.core.mvc.proxy.CurbReverseProxyHandlerMapping;
import curb.server.interceptor.CurbServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@ConditionalOnProperty(name = "curb.reverse-proxy.enabled", havingValue = "true", matchIfMissing = false)
public class CurbServerReverseProxyConfiguration {

    @Bean
    @ConditionalOnMissingBean(CurbReverseProxyHandlerMapping.class)
    public CurbReverseProxyHandlerMapping curbReverseProxyRequestHandler(
            CurbServerProperties properties,
            CurbServerInterceptor interceptor,
            @Autowired(required = false) RestTemplate restTemplate) {
        ReverseProxyProperties reverseProxy = properties.getReverseProxy();
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        CurbReverseProxyHandlerMapping mapping = new CurbReverseProxyHandlerMapping(reverseProxy, restTemplate);
        mapping.setInterceptors(interceptor);
        return mapping;
    }
}
