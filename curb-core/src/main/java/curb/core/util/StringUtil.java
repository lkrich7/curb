package curb.core.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StringUtil {
    ;

    public static boolean isEmpty(CharSequence s) {
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

    /**
     * 去除字符串首尾空白字符
     *
     * @param str 字符串
     * @return 去除空白字符后的字符串，如果str 为 null，则返回 null
     */
    public static String trim(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int beginIndex = 0;
        int endIndex = str.length() - 1;
        while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
            beginIndex++;
        }
        while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
            endIndex--;
        }
        return str.substring(beginIndex, endIndex + 1);
    }

    /**
     * 去除字符串首尾空白字符，如果str 为 null，则返回空字符串
     *
     * @param str 字符串
     * @return 去除空白字符后的字符串
     */
    public static String trimToEmpty(String str) {
        str = trim(str);
        return isEmpty(str) ? "" : str;
    }

    /**
     * 去除字符串首尾空白字符，如果str 为 null，则返回 null
     * @param str
     * @return
     */
    public static String trimToNull(String str) {
        str = trim(str);
        return isEmpty(str) ? null : str;
    }

    /**
     * 分割字符串为数组
     *
     * @param toSplit   待分割字符串
     * @param delimiter 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] split(String toSplit, String delimiter) {
        return StringUtils.delimitedListToStringArray(toSplit, delimiter);
    }

    public static Stream<String> splitToStream(String toSplit, String delimiter) {
        return Arrays.stream(split(toSplit, delimiter));
    }

    public static List<String> splitToList(String toSplit, String delimiter, boolean trim, boolean omitEmpty) {
        Stream<String> stream = splitToStream(toSplit, delimiter);
        if (trim) {
            stream = stream.map(StringUtil::trimToEmpty);
        }
        if (omitEmpty) {
            stream = stream.filter(StringUtil::isNotEmpty);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将集合元素连接成字符串
     * 集合中的null元素将按空字符串处理
     * 不会跳过空字符串
     *
     * @param collection 集合
     * @param delimiter  分隔字符串
     * @param <T>        集合范型
     * @return 连接的字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter) {
        return join(collection, delimiter, false);
    }

    /**
     * 将集合元素连接成字符串
     * 集合中的null元素将按空字符串处理
     *
     * @param collection 集合
     * @param delimiter  分隔字符串
     * @param skipEmpty  是否跳过空字符串
     * @param <T>        集合范型
     * @return 连接的字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter, boolean skipEmpty) {
        return join(collection, delimiter, "", "", skipEmpty);
    }

    /**
     * 将集合元素连接成字符串
     * 集合中的null元素将按空字符串处理
     *
     * @param collection 集合
     * @param delimiter  分隔字符串
     * @param prefix     前缀字符串
     * @param suffix     后缀字符串
     * @param <T>        集合范型
     * @return 连接的字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter, String prefix, String suffix) {
        return join(collection, delimiter, prefix, suffix, false);
    }

    /**
     * 将集合元素连接成字符串
     * 集合中的null元素将按空字符串处理
     *
     * @param collection 集合
     * @param delimiter  分隔字符串
     * @param prefix     前缀字符串
     * @param suffix     后缀字符串
     * @param skipEmpty  是否跳过空字符串
     * @param <T>        集合范型
     * @return 连接的字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter, String prefix, String suffix, boolean skipEmpty) {
        return join(collection, delimiter, prefix, suffix, StringUtil::toString, skipEmpty ? StringUtil::isNotEmpty : null);
    }

    /**
     * 将集合元素连接成字符串
     *
     * @param collection 集合
     * @param delimiter  分隔字符串
     * @param prefix     前缀字符串
     * @param suffix     后缀字符串
     * @param processor  元素转字符串函数
     * @param filter     字符串过滤函数
     * @param <T>        集合范型
     * @return 连接的字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter, String prefix, String suffix,
                                  Function<T, String> processor, Predicate<String> filter) {
        Stream<String> stream = Optional.ofNullable(collection).orElseGet(Collections::emptyList)
                .stream()
                .map(processor);
        if (filter != null) {
            stream = stream.filter(filter);
        }
        return stream.collect(Collectors.joining(delimiter, prefix, suffix));
    }

    public static String toString(Object object) {
        return toString(object, "");
    }

    public static String toString(Object object, String useForNull) {
        return object == null ? useForNull : object.toString();
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }
}
