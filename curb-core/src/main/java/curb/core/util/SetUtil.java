package curb.core.util;

import java.util.LinkedHashSet;
import java.util.Set;

public enum SetUtil {
    ;

    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> ret = new LinkedHashSet<>(set1);
        ret.retainAll(set2);
        return ret;
    }

    public static <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        Set<T> ret = new LinkedHashSet<>(set1);
        ret.removeAll(set2);
        return ret;
    }
}
