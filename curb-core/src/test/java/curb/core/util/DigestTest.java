package curb.core.util;

import junit.framework.TestCase;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class DigestTest extends TestCase {

    public void testMd5DigestAsHex() {
        String str = "Hello World";
        String expected = DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8)).toUpperCase();
        String actual = Digest.MD5.digestAsHex(str, true);
        assertEquals(expected, actual);
    }
}