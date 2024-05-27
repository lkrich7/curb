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

    public Client getClient() {
        return client;
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

}
