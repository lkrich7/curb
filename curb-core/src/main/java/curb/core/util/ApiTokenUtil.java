package curb.core.util;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名工具类
 */
public final class ApiTokenUtil {
    public static final String TOKEN_PARAM_NAME = "apiToken";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiTokenUtil.class);

    private ApiTokenUtil() {
    }

    public static String buildTokenAndPutMap(Map<String, String> paramsMap, String secret) {
        String token = buildToken(paramsMap, secret);
        paramsMap.put(TOKEN_PARAM_NAME, token);
        return token;
    }

    public static String buildToken(Map<String, String> paramsMap, String secret) {
        TreeMap<String, String> allParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            if (TOKEN_PARAM_NAME.equals(key)) {
                continue;
            }
            allParams.put(key, encodeValue(entry.getValue()));
        }
        String rawString = paramMapToRawString(secret, allParams);
        return DigestUtils.md5DigestAsHex(rawString.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkTokenMatched(HttpServletRequest request, String secret) {
        String rawString = buildRawString(request, secret);
        String expectedToken = DigestUtils.md5DigestAsHex(rawString.getBytes(StandardCharsets.UTF_8));

        String token = request.getParameter(TOKEN_PARAM_NAME);
        if (expectedToken.equals(token)) {
            return true;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} mismatch: raw=[{}], expected=[{}], actual=[{}]", TOKEN_PARAM_NAME, rawString, expectedToken, token);
        }
        return false;
    }

    public static String buildRawString(HttpServletRequest request, String secret) {
        TreeMap<String, String> allParams = new TreeMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.equals(TOKEN_PARAM_NAME)) {
                continue;
            }
            allParams.put(name, encodeValue(request.getParameter(name)));
        }
        return paramMapToRawString(secret, allParams);
    }

    private static String encodeValue(String value) {
        return UrlCodec.encodeUtf8(value);
    }

    private static String paramMapToRawString(String secret, TreeMap<String, String> allParams) {
        return Joiner.on('&').withKeyValueSeparator("=").join(allParams) + "&" + secret;
    }
}
