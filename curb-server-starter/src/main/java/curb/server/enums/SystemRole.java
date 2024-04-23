package curb.server.enums;

import curb.core.model.Permission;
import curb.server.po.RolePO;
import curb.server.util.CurbServerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_DELETE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_DISABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_ENABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_SECRET_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_APP_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_GROUP_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_GROUP_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_GROUP_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_GROUP_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_MENU_EDIT_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_MENU_EDIT_SAVE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_BODY_HISTORY_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_BODY_HISTORY_ROLLBACK;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_CONFIG_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_CONFIG_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_DELETE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_DISABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_ENABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_BODY_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PAGE_BODY_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_DELETE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_DISABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_ENABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_ROLE_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_ROLE_SAVE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_PERMISSION_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_DELETE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_DISABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_ENABLE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_PERMISSION_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_PERMISSION_SAVE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_USER_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_ROLE_USER_SAVE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_BLOCK;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_CREATE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_DELETE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_GET;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_RECOVER;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_ROLE_LIST;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_ROLE_SAVE;
import static curb.server.enums.SystemPermissions.SYSTEM_API_USER_UPDATE;
import static curb.server.enums.SystemPermissions.SYSTEM_APPS;
import static curb.server.enums.SystemPermissions.SYSTEM_GROUPS;
import static curb.server.enums.SystemPermissions.SYSTEM_MENUS;
import static curb.server.enums.SystemPermissions.SYSTEM_PAGES;
import static curb.server.enums.SystemPermissions.SYSTEM_PAGE_EDIT;
import static curb.server.enums.SystemPermissions.SYSTEM_PERMISSIONS;
import static curb.server.enums.SystemPermissions.SYSTEM_ROLES;
import static curb.server.enums.SystemPermissions.SYSTEM_USERS;

/**
 * 系统角色
 */
public enum SystemRole {

    ROOT(1, "CURB_ROOT", "超级管理员", EnumSet.of(
            SYSTEM_USERS,
            SYSTEM_API_USER_LIST,
            SYSTEM_API_USER_GET,
            SYSTEM_API_USER_CREATE,
            SYSTEM_API_USER_UPDATE,
            SYSTEM_API_USER_BLOCK,
            SYSTEM_API_USER_RECOVER,
            SYSTEM_API_USER_DELETE,
            SYSTEM_API_USER_ROLE_LIST,
            SYSTEM_API_USER_ROLE_SAVE,

            SYSTEM_ROLES,
            SYSTEM_API_ROLE_LIST,
            SYSTEM_API_ROLE_GET,
            SYSTEM_API_ROLE_CREATE,
            SYSTEM_API_ROLE_UPDATE,
            SYSTEM_API_ROLE_DISABLE,
            SYSTEM_API_ROLE_ENABLE,
            SYSTEM_API_ROLE_DELETE,
            SYSTEM_API_ROLE_USER_LIST,
            SYSTEM_API_ROLE_USER_SAVE,
            SYSTEM_API_ROLE_PERMISSION_LIST,
            SYSTEM_API_ROLE_PERMISSION_SAVE,

            SYSTEM_APPS,
            SYSTEM_API_APP_LIST,
            SYSTEM_API_APP_GET,
            SYSTEM_API_APP_CREATE,
            SYSTEM_API_APP_UPDATE,
            SYSTEM_API_APP_DISABLE,
            SYSTEM_API_APP_ENABLE,
            SYSTEM_API_APP_DELETE,
            SYSTEM_API_APP_SECRET_GET,

            SYSTEM_PAGES,
            SYSTEM_PAGE_EDIT,
            SYSTEM_API_PAGE_LIST,
            SYSTEM_API_PAGE_CREATE,
            SYSTEM_API_PAGE_BODY_GET,
            SYSTEM_API_PAGE_BODY_UPDATE,
            SYSTEM_API_PAGE_BODY_HISTORY_LIST,
            SYSTEM_API_PAGE_BODY_HISTORY_ROLLBACK,
            SYSTEM_API_PAGE_CONFIG_GET,
            SYSTEM_API_PAGE_CONFIG_UPDATE,
            SYSTEM_API_PAGE_ENABLE,
            SYSTEM_API_PAGE_DISABLE,
            SYSTEM_API_PAGE_DELETE,

            SYSTEM_PERMISSIONS,
            SYSTEM_API_PERMISSION_LIST,
            SYSTEM_API_PERMISSION_GET,
            SYSTEM_API_PERMISSION_CREATE,
            SYSTEM_API_PERMISSION_UPDATE,
            SYSTEM_API_PERMISSION_DISABLE,
            SYSTEM_API_PERMISSION_ENABLE,
            SYSTEM_API_PERMISSION_DELETE,
            SYSTEM_API_PERMISSION_ROLE_LIST,
            SYSTEM_API_PERMISSION_ROLE_SAVE,

            SYSTEM_MENUS,
            SYSTEM_API_MENU_EDIT_GET,
            SYSTEM_API_MENU_EDIT_SAVE,

            SYSTEM_GROUPS,
            SYSTEM_API_GROUP_LIST,
            SYSTEM_API_GROUP_GET,
            SYSTEM_API_GROUP_CREATE,
            SYSTEM_API_GROUP_UPDATE
    )),

    GROUP_SYS_ADMIN(2, "CURB_GROUP_SYS_ADMIN", "项目组系统管理员", EnumSet.of(
            SYSTEM_USERS,
            SYSTEM_API_USER_LIST,
            SYSTEM_API_USER_GET,
            SYSTEM_API_USER_CREATE,
            SYSTEM_API_USER_UPDATE,
            SYSTEM_API_USER_BLOCK,
            SYSTEM_API_USER_RECOVER,
            SYSTEM_API_USER_ROLE_LIST,
            SYSTEM_API_USER_ROLE_SAVE,

            SYSTEM_ROLES,
            SYSTEM_API_ROLE_LIST,
            SYSTEM_API_ROLE_GET,
            SYSTEM_API_ROLE_CREATE,
            SYSTEM_API_ROLE_UPDATE,
            SYSTEM_API_ROLE_DISABLE,
            SYSTEM_API_ROLE_ENABLE,
            SYSTEM_API_ROLE_DELETE,
            SYSTEM_API_ROLE_USER_LIST,
            SYSTEM_API_ROLE_USER_SAVE,
            SYSTEM_API_ROLE_PERMISSION_LIST,
            SYSTEM_API_ROLE_PERMISSION_SAVE,

            SYSTEM_APPS,
            SYSTEM_API_APP_LIST,
            SYSTEM_API_APP_GET,
            SYSTEM_API_APP_CREATE,
            SYSTEM_API_APP_UPDATE,
            SYSTEM_API_APP_DISABLE,
            SYSTEM_API_APP_ENABLE,
            SYSTEM_API_APP_DELETE,
            SYSTEM_API_APP_SECRET_GET,

            SYSTEM_PAGES,
            SYSTEM_PAGE_EDIT,
            SYSTEM_API_PAGE_LIST,
            SYSTEM_API_PAGE_CREATE,
            SYSTEM_API_PAGE_BODY_GET,
            SYSTEM_API_PAGE_BODY_UPDATE,
            SYSTEM_API_PAGE_BODY_HISTORY_LIST,
            SYSTEM_API_PAGE_BODY_HISTORY_ROLLBACK,
            SYSTEM_API_PAGE_CONFIG_GET,
            SYSTEM_API_PAGE_CONFIG_UPDATE,
            SYSTEM_API_PAGE_ENABLE,
            SYSTEM_API_PAGE_DISABLE,
            SYSTEM_API_PAGE_DELETE,

            SYSTEM_PERMISSIONS,
            SYSTEM_API_PERMISSION_LIST,
            SYSTEM_API_PERMISSION_GET,
            SYSTEM_API_PERMISSION_CREATE,
            SYSTEM_API_PERMISSION_UPDATE,
            SYSTEM_API_PERMISSION_DISABLE,
            SYSTEM_API_PERMISSION_ENABLE,
            SYSTEM_API_PERMISSION_DELETE,
            SYSTEM_API_PERMISSION_ROLE_LIST,
            SYSTEM_API_PERMISSION_ROLE_SAVE,

            SYSTEM_MENUS,
            SYSTEM_API_MENU_EDIT_GET,
            SYSTEM_API_MENU_EDIT_SAVE,

            SYSTEM_GROUPS,
            SYSTEM_API_GROUP_LIST,
            SYSTEM_API_GROUP_GET,
            SYSTEM_API_GROUP_CREATE,
            SYSTEM_API_GROUP_UPDATE
    )),

    GROUP_BIZ_ADMIN(3, "CURB_GROUP_BIZ_ADMIN", "项目组业务权限管理员", EnumSet.of(
            SYSTEM_USERS,
            SYSTEM_API_USER_LIST,
            SYSTEM_API_USER_GET,
            SYSTEM_API_USER_CREATE,
            SYSTEM_API_USER_UPDATE,
            SYSTEM_API_USER_ROLE_LIST,
            SYSTEM_API_USER_ROLE_SAVE,

            SYSTEM_ROLES,
            SYSTEM_API_ROLE_LIST,
            SYSTEM_API_ROLE_GET,
            SYSTEM_API_ROLE_USER_LIST,
            SYSTEM_API_ROLE_USER_SAVE,
            SYSTEM_API_ROLE_PERMISSION_LIST,

            SYSTEM_APPS,
            SYSTEM_API_APP_LIST,

            SYSTEM_GROUPS,
            SYSTEM_API_GROUP_LIST
    )),
    ;

    public static final List<String> ALL_SIGNS;

    public static final Set<Integer> ALL_ROLE_IDS;

    static {
        Set<Integer> roleIds = new TreeSet<>();
        List<String> signs = new ArrayList<>(values().length);
        for (SystemRole r : values()) {
            signs.add(r.sign);
            roleIds.add(r.roleId);
        }
        ALL_SIGNS = Collections.unmodifiableList(signs);
        ALL_ROLE_IDS = Collections.unmodifiableSet(roleIds);
    }

    private final int roleId;
    private final String sign;
    private final String name;
    private final EnumSet<SystemPermissions> templateSet;

    SystemRole(int roleId, String sign, String name, EnumSet<SystemPermissions> templateSet) {
        this.roleId = roleId;
        this.sign = sign;
        this.name = name;
        this.templateSet = templateSet;
    }

    public static boolean isSystemRoleId(int roleId) {
        return getByRoleId(roleId) != null;
    }

    public static SystemRole getBySign(String sign) {
        for (SystemRole t : values()) {
            if (t.sign.equals(sign)) {
                return t;
            }
        }
        return null;
    }

    public static SystemRole getByRoleId(int roleId) {
        for (SystemRole t : values()) {
            if (t.roleId == roleId) {
                return t;
            }
        }
        return null;
    }

    public static List<Permission> toPermissionList(List<Integer> userSystemRoleId, Integer groupId) {
        List<Permission> list = new ArrayList<>();
        for (SystemRole template : values()) {
            if (userSystemRoleId.contains(template.roleId)) {
                for (SystemPermissions t : template.templateSet) {
                    list.add(t.toPermission(groupId));
                }
            }
        }
        return list;
    }

    /**
     * 判断操作用户否有权限添加指定系统角色
     *
     * @param operators 操作人拥有的系统角色
     * @param granted   要添加的系统角色
     * @return
     */
    public static boolean canGrant(Set<SystemRole> operators, SystemRole granted) {
        for (SystemRole oper : operators) {
            if (oper.roleId < granted.roleId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断操作用户否有权限回收指定系统角色
     *
     * @param operators 操作人拥有的系统角色
     * @param revoked   要回收的系统角色
     * @param isSelf    要修改的用户是否是操作人自己
     * @return
     */
    public static boolean canRevoke(Set<SystemRole> operators, SystemRole revoked, boolean isSelf) {
        for (SystemRole oper : operators) {
            if (oper.roleId < revoked.roleId) {
                return true;
            }
            if (isSelf && oper.roleId <= revoked.roleId) {
                return true;
            }
        }
        return false;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getSign() {
        return sign;
    }

    public String getName() {
        return name;
    }

    public RolePO toRole(Integer groupId) {
        if (this == ROOT) {
            groupId = CurbServerUtil.CURB_GROUP_ID;
        }
        RolePO ret = new RolePO();
        ret.setRoleId(roleId);
        ret.setGroupId(groupId);
        ret.setSign(sign);
        ret.setName(name);
        ret.setDescription(name);
        ret.setState(RoleState.ENABLED.getCode());
        return ret;
    }

}
