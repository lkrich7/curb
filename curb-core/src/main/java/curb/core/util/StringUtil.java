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
        if (str == null) {
            return "";
        }
        str = StringUtils.trimWhitespace(str);
        return str.isEmpty() ? null : str;
    }
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        str = StringUtils.trimWhitespace(str);
        return str.isEmpty() ? null : str;
    }

    public static String[] split(String toSplit, char delimiter) {
        return StringUtils.split(toSplit, String.valueOf(delimiter));
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }
}
