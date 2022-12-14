package curb.client;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * httpclient 连接策略 包装默认实现
 *
 * @see ConnectionKeepAliveStrategy
 * @see DefaultConnectionKeepAliveStrategy
 */
public class WrapDefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    public static final WrapDefaultConnectionKeepAliveStrategy INSTANCE = new WrapDefaultConnectionKeepAliveStrategy();

    /**
     * 缺省保持连接的持续时间(毫秒)
     */
    private final long defaultDuration;

    public WrapDefaultConnectionKeepAliveStrategy() {
        defaultDuration = 5000L;
    }

    public WrapDefaultConnectionKeepAliveStrategy(long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long ret = DefaultConnectionKeepAliveStrategy.INSTANCE.getKeepAliveDuration(response, context);
        return ret < 0 ? defaultDuration : ret;
    }
}
