package curb.client.configuration;

import curb.core.configuration.CurbProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Curb 客户端配置属性
 */
@ConfigurationProperties("curb")
public class CurbClientProperties extends CurbProperties {

    /**
     * Curb 客户端模式配置
     */
    private final Client client = new Client();

    /**
     * 反向代理转发配置
     */
    private final ReverseProxy reverseProxy = new ReverseProxy();

    public Client getClient() {
        return client;
    }

    public ReverseProxy getReverseProxy() {
        return reverseProxy;
    }

    public static class Client {

        /**
         * 服务器域名
         */
        private String server;

        /**
         * APPID
         */
        private String appid;

        /**
         * 参数签名密钥
         */
        private String secret;


        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class ReverseProxy {

        /**
         * 是否启用反向代理转发
         */
        boolean enabled = false;

        /**
         * 转发url映射
         */
        private String[] urlMappings = new String[]{
                "/**",
        };

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getUrlMappings() {
            return urlMappings;
        }

        public void setUrlMappings(String[] urlMappings) {
            this.urlMappings = urlMappings;
        }
    }

}
