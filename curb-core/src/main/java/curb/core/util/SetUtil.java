package curb.core.util;

import com.google.common.collect.Sets;

import java.util.Set;

public enum SetUtil {
    ;

    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        return Sets.intersection(set1, set2);
    }

    public static <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        return Sets.difference(set1, set2);
    }
}
