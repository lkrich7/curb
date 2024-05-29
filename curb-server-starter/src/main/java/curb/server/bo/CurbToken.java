package curb.server.bo;

import curb.core.util.Crypto;
import curb.core.util.JsonUtil;

import java.util.Objects;

/**
 * 用户登录态令牌
 */
public class CurbToken {

    /**
     * 令牌的key
     */
    private String key;

    /**
     * 令牌生成时间戳
     */
    private long ts;

    /**
     * 携带的扩展数据
     */
    private String payload;

    public CurbToken() {
        this(null, null);
    }

    public CurbToken(String key, String payload) {
        this(key, payload, System.currentTimeMillis());
    }

    public CurbToken(String key, String payload, long ts) {
        this.key = key;
        this.payload = payload;
        this.ts = ts;
    }

    public static CurbToken decrypt(String encrypted, String secret, Crypto crypto) {
        try {
            String rawString = crypto.decrypt(encrypted, secret);
            return parseRawString(rawString);
        } catch (Exception e) {
            return null;
        }
    }

    private static CurbToken parseRawString(String rawString) {
        return JsonUtil.parseObject(rawString, CurbToken.class);
    }

    private static String buildRawString(CurbToken token) {
        return JsonUtil.toJSONString(token);
    }

    public String encrypt(String secret, Crypto crypto) {
        String rawString = buildRawString(this);
        return crypto.encrypt(rawString, secret);
    }

    public boolean isExpired(long ttlMs) {
        return ts < System.currentTimeMillis() - ttlMs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CurbToken)) return false;
        CurbToken curbToken = (CurbToken) object;
        return ts == curbToken.ts && Objects.equals(key, curbToken.key) && Objects.equals(payload, curbToken.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, ts, payload);
    }

    @Override
    public String toString() {
        return "CurbToken{" +
                "key='" + key + '\'' +
                ", ts=" + ts +
                ", payload=" + payload +
                '}';
    }

}
