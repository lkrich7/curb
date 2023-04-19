package curb.server.configuration;

import curb.core.configuration.CurbProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Curb 服务端配置属性类
 */
@ConfigurationProperties("curb")
public class CurbServerProperties extends CurbProperties {

    public CurbServerProperties() {
        this.setIncludePathPatterns(new String[]{
                "/**",
        });
        this.setExcludePathPatterns(new String[]{
                "/favicon.ico",
                "/amis/**",
                "/amis-editor/**",
                "/static/**",
                "/login",
                "/logout",
                "/api/**",
        });
    }

}
