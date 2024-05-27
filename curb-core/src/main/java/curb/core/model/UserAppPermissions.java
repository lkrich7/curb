package curb.core.model;

import curb.core.CurbAccessConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 用户在一个应用下的全部权限定义
 */
public class UserAppPermissions {

    private static final UserAppPermissions NONE = new UserAppPermissions(null);
    private final Map<Permission, Boolean> permissions;

    private UserAppPermissions(Map<Permission, Boolean> permissions) {
        this.permissions = permissions;
    }

    public static UserAppPermissions none() {
        return NONE;
    }

    public static UserAppPermissions build(List<UserPermission> permissions) {
        if (permissions == null) {
            return NONE;
        }
        Map<Permission, Boolean> map = new TreeMap<>();
        addUserPermission(map, permissions);

        return new UserAppPermissions(map);
    }

    public static UserAppPermissions build(Collection<Permission> permissions) {
        if (permissions == null) {
            return NONE;
        }
        Map<Permission, Boolean> map = new TreeMap<>();
        for (Permission permission : permissions) {
            map.put(permission, true);
        }
        return new UserAppPermissions(map);
    }

    public static UserAppPermissions buildWithSigns(Collection<String> signs) {
        if (signs == null) {
            return NONE;
        }
        Map<Permission, Boolean> map = new TreeMap<>();
        for (String sign : signs) {
            map.put(Permission.build(sign), true);
        }
        return new UserAppPermissions(map);
    }

    public static UserAppPermissions build(Collection<Permission> perms1, List<UserPermission> perms2) {
        UserAppPermissions ret = build(perms1);
        if (ret == NONE) {
            return build(perms2);
        }
        addUserPermission(ret.permissions, perms2);
        return ret;
    }

    private static void addUserPermission(Map<Permission, Boolean> map, List<UserPermission> permissions) {
        for (UserPermission userPermission : permissions) {
            Permission permission = Permission.build(userPermission.getSign());
            boolean userChecked = userPermission.isUserChecked();
            if (!userChecked) {
                // 防止重复覆盖
                Boolean existed = map.get(permission);
                if (existed != null && existed) {
                    userChecked = true;
                }
            }
            map.put(permission, userChecked);
        }
    }

    public PermissionResult check(Permission request) {
        return check(request, true);
    }

    public PermissionResult check(Permission request, CurbAccessConfig config) {
        return check(request, config.isCheckParam(), config.getIgnoreParamNames());
    }

    /**
     * 权限检查
     *
     * @param request
     * @param checkParam
     * @param ignoreParamNames
     * @return
     */
    public PermissionResult check(Permission request, boolean checkParam, String... ignoreParamNames) {
        if (request == null) {
            return new PermissionResult(null, this);
        }
        List<Permission> allowedPermissions = new ArrayList<>();
        if (permissions != null) {
            for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                Permission permission = entry.getKey();
                if (permission.isAllowed(request, checkParam, ignoreParamNames)) {
                    Boolean isUserChecked = entry.getValue();
                    if (isUserChecked != null && isUserChecked) {
                        allowedPermissions.add(permission);
                    }
                }
            }
        }
        return new PermissionResult(request, this, allowedPermissions);
    }

    public boolean isAllowed(Permission request) {
        return check(request, true).isAuthorized();
    }

}
