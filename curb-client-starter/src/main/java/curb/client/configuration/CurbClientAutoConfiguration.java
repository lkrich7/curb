package curb.client.configuration;

import curb.core.mvc.CurbWebMvcConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Curb客户端自动配置类
 */
@Configuration
@ConditionalOnProperty(name = "curb.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CurbClientProperties.class)
@Import({
        CurbClientConfiguration.class,
        CurbWebMvcConfigurer.class,
})
public class CurbClientAutoConfiguration {

}
