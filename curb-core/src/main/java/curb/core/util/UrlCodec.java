package curb.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public final class UrlCodec {

    private UrlCodec() {
    }

    public static String encodeUtf8(String content) {
        return encode(content, StandardCharsets.UTF_8);
    }

    public static String encode(String content, Charset charset) {
        return encode(content, charset.name());
    }

    public static String encode(String content, String charset) {
        try {
            return URLEncoder.encode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(charset);
        }
    }

    public static String decodeUtf8(String content) {
        return decode(content, StandardCharsets.UTF_8);
    }

    public static String decode(String content, Charset charset) {
        return decode(content, charset.name());
    }

    public static String decode(String content, String charset) {
        try {
            return URLDecoder.decode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(charset);
        }
    }


}
