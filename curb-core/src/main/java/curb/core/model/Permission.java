package curb.core.model;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import curb.core.util.StringUtil;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 权限定义类
 */
public final class Permission implements Comparable<Permission>, Serializable {

    public static final String WILDCARD_TOKEN = "*";

    private static final char PATH_PART_DIVIDER = '/';
    private static final char SUB_PART_DIVIDER = ',';
    private static final char PARAM_LEAD_TOKEN = '?';
    private static final char PARAM_DIV_TOKEN = '&';
    private static final char PARAM_KV_TOKEN = '=';

    private static final Splitter PATH_PART_SPLITTER = Splitter.on(PATH_PART_DIVIDER).omitEmptyStrings().trimResults();
    private static final Splitter SUB_PART_SPLITTER = Splitter.on(SUB_PART_DIVIDER).omitEmptyStrings().trimResults();
    private static final Splitter PARAM_SPLITTER = Splitter.on(PARAM_DIV_TOKEN).omitEmptyStrings().trimResults();

    private final ArrayList<TreeSet<String>> path;
    private final TreeMap<String, TreeSet<String>> params;

    private final String sign;

    private Permission(ArrayList<TreeSet<String>> path, TreeMap<String, TreeSet<String>> params) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty");
        }
        if (params == null) {
            params = new TreeMap<>();
        }

        this.path = path;
        this.params = params;
        this.sign = buildSign();
    }

    public static Permission build(String sign) {
        sign = StringUtil.trimToNull(sign);
        if (sign == null) {
            return null;
        }
        try {
            URI uri = URI.create(sign);
            String path = uri.getPath();
            String query = uri.getRawQuery();
            ArrayList<TreeSet<String>> pathParts = parsePath(path);
            TreeMap<String, TreeSet<String>> paramParts = parseParams(query);
            return new Permission(pathParts, paramParts);
        } catch (Exception e) {
            return null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSign() {
        return sign;
    }

    @Override
    public int compareTo(Permission o) {
        return sign.compareTo(o.sign);
    }

    @Override
    public String toString() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Permission that = (Permission) o;

        return this.sign.equals(that.sign);
    }

    @Override
    public int hashCode() {
        return sign.hashCode();
    }

    /**
     * 判断请求的权限是否能通过验证
     *
     * @param request 请求的权限
     * @return
     */
    public boolean isAllowed(Permission request, boolean checkParam, String... ignoreParamNames) {
        if (request == null) {
            return false;
        }

        // 验证路径
        if (!isPathAllowed(request.path)) {
            return false;
        }

        if (!checkParam) {
            return true;
        }
        return isParamAllowed(request.params, ignoreParamNames);
    }

    public boolean containsParam(String name, String param) {
        Set<String> set = params.get(name);
        if (set == null || set.isEmpty() || set.contains(WILDCARD_TOKEN)) {
            return true;
        }
        return set.contains(param);
    }

    TreeSet<String> getParams(String name) {
        return this.params.get(name);
    }

    private boolean isPathAllowed(List<TreeSet<String>> requestPathPart) {
        int i = 0;
        for (Set<String> reqPart : requestPathPart) {
            if (i > this.path.size() - 1) {
                // 本权限的路径全部分段都验证通过了，验证通过
                return true;
            }
            Set<String> thisPart = this.path.get(i);
            if (!thisPart.contains(WILDCARD_TOKEN) && !thisPart.containsAll(reqPart)) {
                // 本权限的路径当前分段验证不通过
                return false;
            }
            i++;
        }
        // 请求路径的全部分段都验证通过了，本权限的路径还有分段剩余未做匹配，要检查是否剩余的都是通配符
        for (; i < this.path.size(); i++) {
            Set<String> part = this.path.get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
                return false;
            }
        }
        return true;
    }

    private boolean isParamAllowed(Map<String, TreeSet<String>> requestParams, String... ignoreParamNames) {
        Set<String> ignoreParamSet;
        if (ignoreParamNames == null || ignoreParamNames.length < 1) {
            ignoreParamSet = Collections.emptySet();
        } else {
            ignoreParamSet = Sets.newHashSet(ignoreParamNames);
        }

        for (Map.Entry<String, TreeSet<String>> param : this.params.entrySet()) {
            String name = param.getKey();
            if (ignoreParamSet.contains(name)) {
                // 跳过要忽略的参数
                continue;
            }
            Set<String> values = param.getValue();
            if (values == null || values.isEmpty() || values.contains(WILDCARD_TOKEN)) {
                // 本权限中参数值为空或者是通配符，验证通过，继续比较
                continue;
            }
            Set<String> requestValues = requestParams.get(name);
            if (requestValues == null || requestValues.isEmpty()) {
                // 权限的参数中该参数在请求的参数中不存在，验证不通过
                return false;
            }
            if (!values.containsAll(requestValues)) {
                // 当前参数的请求值在本权限设定的值中有不存在的值，验证不通过
                return false;
            }
        }

        return true;
    }

    private static ArrayList<TreeSet<String>> parsePath(String path) {
        List<String> parts = PATH_PART_SPLITTER.splitToList(path);
        ArrayList<TreeSet<String>> ret = new ArrayList<>(parts.size());
        for (String part : parts) {
            List<String> subParts = SUB_PART_SPLITTER.splitToList(part);
            TreeSet<String> set = new TreeSet<>(subParts);
            ret.add(set);
        }
        return ret;
    }

    private static TreeMap<String, TreeSet<String>> parseParams(String param) {
        TreeMap<String, TreeSet<String>> ret = new TreeMap<>();
        if (param == null) {
            return ret;
        }
        List<String> parts = PARAM_SPLITTER.splitToList(param);
        for (String part : parts) {
            String[] kv = StringUtil.split(part, PARAM_KV_TOKEN);
            if (kv.length < 1) {
                continue;
            }
            String key = StringUtil.trimToEmpty(kv[0]);
            TreeSet<String> valueSet = ret.get(key);
            if (valueSet == null) {
                valueSet = new TreeSet<>();
                ret.put(key, valueSet);
            }
            for (int i = 1; i < kv.length; i++) {
                List<String> values = SUB_PART_SPLITTER.splitToList(kv[i]);
                if (values != null) {
                    valueSet.addAll(values);
                }
            }
        }
        return ret;
    }

    private String buildSign() {
        Joiner setJoiner = Joiner.on(SUB_PART_DIVIDER).skipNulls();
        StringBuilder builder = new StringBuilder();
        for (Set<String> subParts : path) {
            builder.append(PATH_PART_DIVIDER);
            setJoiner.appendTo(builder, subParts);
        }
        if (params != null) {
            Iterator<Map.Entry<String, TreeSet<String>>> iter = params.entrySet().iterator();
            for (char token = PARAM_LEAD_TOKEN; iter.hasNext(); token = PARAM_DIV_TOKEN) {
                Map.Entry<String, TreeSet<String>> entry = iter.next();
                TreeSet<String> value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    builder.append(token);
                    builder.append(entry.getKey());
                    builder.append(PARAM_KV_TOKEN);
                    setJoiner.appendTo(builder, entry.getValue());
                }
            }
        }
        return builder.toString();
    }

    public static final class Builder {
        private ArrayList<TreeSet<String>> pathPart;
        private TreeMap<String, TreeSet<String>> paramPart;

        private Builder() {
            init();
        }

        public Builder path(String path) {
            pathPart = parsePath(path);
            return this;
        }

        public Builder addParam(String name, String value) {
            TreeSet<String> set = checkParamSet(name);
            set.add(value);
            return this;
        }

        public Builder addParams(String name, String[] values) {
            TreeSet<String> set = checkParamSet(name);
            Collections.addAll(set, values);
            return this;
        }

        public Builder addParams(String name, Collection<String> values) {
            TreeSet<String> set = checkParamSet(name);
            set.addAll(values);
            return this;
        }

        public Permission build() {
            Permission ret = new Permission(pathPart, paramPart);
            init();
            return ret;
        }

        private void init() {
            this.pathPart = null;
            this.paramPart = null;
        }

        private TreeSet<String> checkParamSet(String name) {
            if (paramPart == null) {
                paramPart = new TreeMap<>();
            }
            TreeSet<String> set = paramPart.get(name);
            if (set == null) {
                set = new TreeSet<>();
                paramPart.put(name, set);
            }
            return set;
        }

    }
}
