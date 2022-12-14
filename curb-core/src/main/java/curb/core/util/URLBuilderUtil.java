package curb.core.util;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Url构造工具类
 */
public final class URLBuilderUtil {

    private URLBuilderUtil() {
    }

    public static String joinToGet(String url, Map<String, String> params, String encoding) {
        String paramStr = joinParams(params, encoding);
        if (StringUtils.isBlank(paramStr)) {
            return url;
        }
        String spliter = url.lastIndexOf('?') < 0 ? "?" : "&";
        return url + spliter + paramStr;
    }

    public static String joinParams(Map<String, String> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        Map<String, String> encodedParams;
        if (encoding == null) {
            encodedParams = params;
        } else {
            encodedParams = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String encodedValue = UrlCodec.encode(entry.getValue(), encoding);
                encodedParams.put(entry.getKey(), encodedValue);
            }
        }
        return Joiner.on('&').withKeyValueSeparator("=").join(encodedParams);
    }

}
