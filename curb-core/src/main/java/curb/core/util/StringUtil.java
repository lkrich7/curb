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
