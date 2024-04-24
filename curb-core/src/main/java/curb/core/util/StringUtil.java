package curb.core.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StringUtil {
    ;

    private static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence s) {
        return !isEmpty(s);
    }

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
     *
     * @param toSplit
     * @param delimiter
     * @return
     */
    public static String[] split(String toSplit, char delimiter) {
        return split(toSplit, String.valueOf(delimiter));
    }

    public static String[] split(String toSplit, String delimiter) {
        return StringUtils.delimitedListToStringArray(toSplit, delimiter);
    }

    public static List<String> splitToList(String toSplit, String delimiter, boolean trim, boolean omitEmpty) {
        Stream<String> stream = Arrays.stream(StringUtils.delimitedListToStringArray(toSplit, delimiter));
        if (trim) {
            stream = stream.map(StringUtil::trimToEmpty);
        }
        if (omitEmpty) {
            stream = stream.filter(StringUtil::isNotEmpty);
        }
        return stream.collect(Collectors.toList());
    }

    public static <T> String join(String delimiter, boolean skipNull, Collection<T> collection) {
        Stream<T> stream = collection.stream();
        if (skipNull) {
            stream = stream.filter(Objects::nonNull);
        }
        return stream.map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }
}
