package curb.server.configuration;

import curb.core.AccessLevel;
import curb.core.configuration.CurbProperties;
import curb.core.configuration.ReverseProxyProperties;
import curb.core.util.Crypto;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Curb 服务端配置属性类
 */
@ConfigurationProperties("curb")
public class CurbServerProperties extends CurbProperties {

    private TokenProperties token = new TokenProperties();

    public CurbServerProperties() {
        this.setIncludePathPatterns(new String[]{
                "/**",
        });
        this.setExcludePathPatterns(new String[]{
                "/favicon.ico",
                "/amis/**",
                "/amis-editor/**",
                "/static/**",
                "/api/**",
        });

        ReverseProxyProperties reverseProxy = new ReverseProxyProperties();
        reverseProxy.setAccessLevel(AccessLevel.PERMISSION);
        reverseProxy.setOrder(0);
        this.setReverseProxy(reverseProxy);
    }

    public TokenProperties getToken() {
        return token;
    }

    public void setToken(TokenProperties token) {
        this.token = token;
    }

    public static class TokenProperties {
        private long ttl = 3600 * 1000;
        private Crypto algorithm = Crypto.AES;

        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }

        public Crypto getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(Crypto algorithm) {
            this.algorithm = algorithm;
        }
    }

}
