package curb.core.util;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum IOUtil {
    ;

    public static void copy(InputStream in, OutputStream out) throws IOException {
        StreamUtils.copy(in, out);
    }

    public static String copyToString(InputStream in) throws IOException {
        return copyToString(in, StandardCharsets.UTF_8);
    }

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        return StreamUtils.copyToString(in, charset);
    }

}
