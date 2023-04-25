package curb.server.bo;

import curb.core.util.JsonUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户登录态令牌
 */
public class CurbToken {
    /**
     * 用户类型
     */
    private Integer type;
    /**
     * 登录用户名
     */
    private String username;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 令牌生成时间戳
     */
    private Long ts;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public static CurbToken decrypt(String encrypted, String secret) {
        try {
            String rawString = aesDecrypt(encrypted, secret);
            return parseRawString(rawString);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(CurbToken token, String secret) {
        try {
            String rawString = buildRawString(token);
            return aesEncrypt(rawString, secret);
        } catch (Exception e) {
            return null;
        }
    }

    private static CurbToken parseRawString(String rawString) {
        return JsonUtil.parseObject(rawString, CurbToken.class);
    }

    private static String buildRawString(CurbToken token) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", token.getType());
        map.put("username", token.getUsername());
        map.put("name", token.getName());
        map.put("ts", token.getTs());
        return JsonUtil.toJSONString(map);
    }

    private static String aesDecrypt(String encrypted, String keyStr) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] input = Base64.getDecoder().decode(encrypted);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, initKeyForAES(keyStr));
        byte[] output = cipher.doFinal(input);
        return new String(output, StandardCharsets.UTF_8);
    }

    private static String aesEncrypt(String rawString, String keyStr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] input = rawString.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, initKeyForAES(keyStr));
        byte[] output = cipher.doFinal(input);
        return Base64.getUrlEncoder().encodeToString(output);
    }

    private static Key initKeyForAES(String keyStr) throws NoSuchAlgorithmException {
        if (null == keyStr || keyStr.isEmpty()) {
            throw new IllegalArgumentException("keyStr should not be empty");
        }
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(keyStr.getBytes());
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        return new SecretKeySpec(enCodeFormat, "AES");
    }
}
