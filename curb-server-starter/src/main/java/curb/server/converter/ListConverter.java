package curb.server.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * List转换器
 */
public enum ListConverter {
    ;

    public static <T, R> List<R> convert(Collection<T> list, Function<T, R> function) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<R> ret = new ArrayList<>(list.size());
        for (T t : list) {
            ret.add(function.apply(t));
        }
        return ret;
    }

}
