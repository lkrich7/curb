package curb.core.model;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 权限检查结果
 */
public class PermissionResult {

    /**
     * 当前用户请求的权限
     */
    private Permission request;

    /**
     * 用户在一个应用下的所有权限定义
     */
    private UserAppPermissions userAppPermissions;

    /**
     * 允许用户的权限
     */
    private List<Permission> allowedPermissions;

    PermissionResult(Permission request, UserAppPermissions userAppPermissions) {
        this(request, userAppPermissions, null);
    }

    PermissionResult(Permission request, UserAppPermissions userAppPermissions, List<Permission> allowedPermissions) {
        if (allowedPermissions == null) {
            allowedPermissions = Collections.emptyList();
        }
        this.request = request;
        this.userAppPermissions = userAppPermissions;
        this.allowedPermissions = Collections.unmodifiableList(allowedPermissions);
    }

    /**
     * 是否通过权限认证
     *
     * @return
     */
    public boolean isAuthorized() {
        return allowedPermissions != null && !allowedPermissions.isEmpty();
    }

    /**
     * 获取请求权限对象
     *
     * @return
     */
    public Permission getRequest() {
        return request;
    }

    /**
     * 获取用户应用权限对象
     *
     * @return
     */
    public UserAppPermissions getUserAppPermissions() {
        return userAppPermissions;
    }

    /**
     * 获取通过验证的权限对象
     *
     * @return
     */
    public List<Permission> getAllowed() {
        return allowedPermissions;
    }

    /**
     * 通过验证的权限对象中是否包含请求中的指定参数的全部值
     *
     * @param name
     * @return
     */
    public boolean containsAllParams(String name) {
        return containsAllParams(name, request.getParams(name));
    }

    /**
     * 通过的权限对象中是否包含指定的全部参数值
     *
     * @param name
     * @param values
     * @return
     */
    public boolean containsAllParams(String name, Collection<String> values) {
        for (String value : values) {
            if (!containsParam(name, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过验证的权限对象中是否包含指定参数值
     *
     * @param name
     * @param value
     * @return
     */
    public boolean containsParam(String name, String value) {
        for (Permission permission : allowedPermissions) {
            if (permission.containsParam(name, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取通过验证的权限对象中指定参数的全部取值集合
     *
     * @param name 参数名
     * @return 参数值的集合，注意，如果name指定的参数在通过验证的权限中没有具体的值，将返回一个含有通配符Permission.WILDCARD_TOKEN的集合
     */
    public Set<String> getParamSet(String name) {
        Set<String> ret = new TreeSet<>();
        if (allowedPermissions == null || allowedPermissions.isEmpty()) {
            return ret;
        }
        for (Permission permission : allowedPermissions) {
            TreeSet<String> set = permission.getParams(name);
            if (set != null) {
                ret.addAll(set);
            }
        }
        if (ret.isEmpty()) {
            ret.add(Permission.WILDCARD_TOKEN);
        }
        return ret;
    }

    /**
     * 将通过验证的权限对象中的指定参数集合与请求中的取交集
     *
     * @param name
     * @return
     */
    public Set<String> intersectParamSet(String name) {
        return intersectParamSet(name, request.getParams(name));
    }

    /**
     * 将通过验证的权限对象中的指定参数集合与给定的参数值集合取交集
     *
     * @param name
     * @param values
     * @return
     */
    public Set<String> intersectParamSet(String name, Collection<String> values) {
        Set<String> set1 = getParamSet(name);
        Set<String> set2 = values == null ? Collections.<String>emptySet() : new TreeSet<>(values);
        if (set1 == null || set1.isEmpty() || set1.contains(Permission.WILDCARD_TOKEN)) {
            return set2;
        }
        return Sets.intersection(set1, set2);
    }

}
