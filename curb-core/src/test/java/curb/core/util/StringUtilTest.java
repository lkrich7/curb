package curb.core.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StringUtilTest {

    @Test
    public void split() {
        String[] ret = StringUtil.split(" ", ',');
        assertTrue(ret.length == 0);
        ret = StringUtil.split(",", ',');
        assertTrue(ret.length == 0);
        ret = StringUtil.split(" 1,2 ,  , 3 , ,", ',');
        assertEquals(Arrays.asList("1","2","3"), ret);
    }
}