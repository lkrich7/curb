package curb.core.util;

import java.util.Collection;
import java.util.Map;

public enum CollectionUtil {
    ;

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> boolean isEmpty(Map<T, T> map) {
        return map == null || map.isEmpty();
    }
    public static <T> boolean isNotEmpty(Map<T, T> map) {
        return !isEmpty(map);
    }
}
