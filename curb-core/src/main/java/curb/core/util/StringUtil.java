package curb.core.util;

import org.springframework.util.StringUtils;

public enum StringUtil {
    ;

    public static boolean isBlank(CharSequence cs) {
        if (cs == null) {
            return true;
        }
        if (cs.length() == 0) {
            return true;
        }
        for (int i = 0, strLen = cs.length(); i < strLen; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static String trimToEmpty(String str) {
        str = StringUtils.trimWhitespace(str);
        return str == null ? "" : str;
    }
    public static String trimToNull(String str) {
        str = StringUtils.trimWhitespace(str);
        return str == null || str.isEmpty() ? null : str;
    }

    /**
     * 分割字符串
     * @param toSplit
     * @param delimiter
     * @return
     */
    public static String[] split(String toSplit, char delimiter) {
        return StringUtils.delimitedListToStringArray(toSplit, String.valueOf(delimiter));
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }
}
