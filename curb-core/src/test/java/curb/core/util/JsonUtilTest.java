package curb.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonUtilTest {

    public static class C1 {
        public String f1;
        public int f2;
    }

    public static class C2 {
        public C1 c1;
        public String s1;
    }

    public static class C3 {
        public String c1;
        public String s1;
    }
    @Test
    public void parseObject() {
        C1 c1 = new C1();
        c1.f1 = "hello";
        c1.f2 = 42;

        C2 c2 = new C2();
        c2.c1 = c1;
        c2.s1 = "world";
        String str = JsonUtil.toJSONString(c2);
        System.err.println(str);

        C2 c2s = JsonUtil.parseObject(str, C2.class);
        String str2 = JsonUtil.toJSONString(c2s);
        assertEquals("序列化+反序列化+序列化值相同", str, str2);

        String comment = "{\"c1\":{\"f1\":\"hello\",//\n\"f2\":42},/*注释*/\"s1\":\"world\"}";
        C2 c2c = JsonUtil.parseObject(comment, C2.class);
        assertEquals("支持JSON字符串中含有注释", str, JsonUtil.toJSONString(c2c));
    }

    @Test
    public void testParseObject() {
    }

    @Test
    public void testParseObject1() {
    }

    @Test
    public void parseArray() {
    }

    @Test
    public void toJSONString() {
    }

    @Test
    public void testToJSONString() {
    }
}