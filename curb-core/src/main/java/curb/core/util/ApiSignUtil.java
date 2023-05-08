package curb.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 参数签名工具类
 */
public enum ApiSignUtil {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSignUtil.class);
    private static final String SIGN_PARAM_NAME = "sign";

    public static String sign(Map<String, String> paramsMap, String secret) {
        TreeMap<String, String> sortedParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            if (SIGN_PARAM_NAME.equals(key)) {
                continue;
            }
            sortedParams.put(key, entry.getValue());
        }
        String digest = joinParams(sortedParams);
        return makeSign(digest, secret);
    }

    public static String signAndPutMap(Map<String, String> paramsMap, String secret) {
        String signature = sign(paramsMap, secret);
        paramsMap.put(SIGN_PARAM_NAME, signature);
        return signature;
    }

    public static String signAndJoinToUrl(String path, Map<String, String> paramsMap, String secret) {
        signAndPutMap(paramsMap, secret);
        String paramStr = joinParams(paramsMap);
        if (StringUtil.isBlank(paramStr)) {
            return path;
        }
        return path + "?" + paramStr;
    }

    public static boolean checkSignMatched(HttpServletRequest request, String secret) {
        String actual = request.getParameter(SIGN_PARAM_NAME);
        if (actual == null || actual.isEmpty()) {
            return false;
        }
        String digest = buildDigest(request);
        String expected = makeSign(digest, secret);
        if (expected.equals(actual)) {
            return true;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("signature mismatch: digest=[{}] expected=[{}], actual=[{}]", digest, expected, actual);
        }
        return false;
    }

    private static String makeSign(String digest, String secret) {
        digest = digest + "&" + secret;
        return DigestUtils.md5DigestAsHex(digest.getBytes(StandardCharsets.UTF_8));
    }

    private static String buildDigest(HttpServletRequest request) {
        TreeMap<String, String> sortedParams = new TreeMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.equals(SIGN_PARAM_NAME)) {
                continue;
            }
            sortedParams.put(name, request.getParameter(name));
        }
        return joinParams(sortedParams);
    }

    private static String joinParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        String ret = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
        return ret;
    }

    private static String encode(String value) {
        return UrlCodec.encodeUtf8(value);
    }

}
