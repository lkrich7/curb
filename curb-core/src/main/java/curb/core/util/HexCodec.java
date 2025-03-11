package curb.core.util;

public enum HexCodec {
    ;

    private static final byte[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组编码为十六进制字符串（默认小写）
     */
    public static String encode(byte[] src) {
        return encode(src, false);
    }

    /**
     * 将字节数组编码为十六进制字符串（可指定大小写）
     */
    public static String encode(byte[] data, boolean upperCase) {
        byte[] digits = upperCase ? DIGITS_UPPER : DIGITS_LOWER;
        byte[] out = new byte[data.length << 1];
        for (int i = 0; i < data.length; i++) {
            out[i << 1] = digits[(data[i] & 0xF0) >>> 4];
            out[(i << 1) + 1] = digits[data[i] & 0x0F];
        }
        return new String(out);
    }

    public static byte[] decode(String source) {
        source = StringUtil.trimToEmpty(source);
        int len = source.length();
        if ((len & 1) != 0) {
            throw new IllegalArgumentException("Invalid hex length: " + len);
        }
        byte[] out = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            int high = hexToCode(source, i);
            int low = hexToCode(source, i + 1);
            out[i >> 1] = (byte) ((high << 4) | low);
        }
        return out;
    }

    private static int hexToCode(String source, int index) {
        char hex = source.charAt(index);
        if (hex >= '0' && hex <= '9') {
            return hex - '0';
        }
        if (hex >= 'a' && hex <= 'f') {
            return hex - 'a' + 10;
        }
        if (hex >= 'A' && hex <= 'F') {
            return hex - 'A' + 10;
        }
        throw new IllegalArgumentException("Invalid hex char at position " + index + ": " + source);
    }
}
