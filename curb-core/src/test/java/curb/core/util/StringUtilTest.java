package curb.core.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void testTrimToEmpty() {
        assertEquals("", StringUtil.trimToEmpty(null));
        assertEquals("", StringUtil.trimToEmpty(""));
        assertEquals("hello world", StringUtil.trimToEmpty("\u00A0 \t \r \n hello world \t \t \n \r  "));
    }

    @Test
    public void testSplit() {
        String[] ret = StringUtil.split(" ", ",");
        assertEquals(1, ret.length);
        ret = StringUtil.split(",", ",");
        assertEquals(2, ret.length);
        ret = StringUtil.split(" 1,2 ,  , 3 , ,", ",");
        assertArrayEquals(new String[]{" 1", "2 ", "  ", " 3 ", " ", ""}, ret);
    }

    @Test
    public void testSplitToList() {
        String toSplit = " 1,2 ,  , 3 , ,";
        assertEquals(Arrays.asList(" 1", "2 ", "  ", " 3 ", " ", ""), StringUtil.splitToList(toSplit, ",", false, false));
        assertEquals(Arrays.asList(" 1", "2 ", "  ", " 3 ", " "), StringUtil.splitToList(toSplit, ",", false, true));
        assertEquals(Arrays.asList("1", "2", "", "3", "", ""), StringUtil.splitToList(toSplit, ",", true, false));
        assertEquals(Arrays.asList("1", "2", "3"), StringUtil.splitToList(toSplit, ",", true, true));
    }

    @Test
    public void testJoin() {
        List<Integer> list = Arrays.asList(null, null, 1, null, null, 2, null, null);
        assertEquals(",,1,,,2,,", StringUtil.join(list, ","));
        assertEquals(",,1,,,2,,", StringUtil.join(list, ",", false));
        assertEquals("1,2", StringUtil.join(list, ",", true));

        assertEquals("[,,1,,,2,,]", StringUtil.join(list, ",", "[", "]"));
        assertEquals("[,,1,,,2,,]", StringUtil.join(list, ",", "[", "]", false));
        assertEquals("[1,2]", StringUtil.join(list, ",", "[", "]", true));
    }
}