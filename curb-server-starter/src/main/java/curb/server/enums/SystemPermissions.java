package curb.server.enums;

import curb.core.model.Permission;
import org.apache.commons.lang3.StringUtils;

/**
 * 权限模版
 */
public enum SystemPermissions {
    SYSTEM_USERS("/system/users", "用户管理"),
    SYSTEM_API_USER_LIST("/system/api/user/list", "用户列表查询"),
    SYSTEM_API_USER_GET("/system/api/user/get", "用户详情查询"),
    SYSTEM_API_USER_CREATE("/system/api/user/create", "新建用户"),
    SYSTEM_API_USER_UPDATE("/system/api/user/update", "修改用户信息"),
    SYSTEM_API_USER_BLOCK("/system/api/user/block", "停用用户"),
    SYSTEM_API_USER_RECOVER("/system/api/user/recover", "启用用户"),
    SYSTEM_API_USER_DELETE("/system/api/user/delete", "删除用户"),
    SYSTEM_API_USER_ROLE_LIST("/system/api/user/role/list", "用户角色查询"),
    SYSTEM_API_USER_ROLE_SAVE("/system/api/user/role/save", "用户角色修改"),

    SYSTEM_ROLES("/system/roles", "角色管理"),
    SYSTEM_API_ROLE_LIST("/system/api/role/list", "角色列表查询"),
    SYSTEM_API_ROLE_GET("/system/api/role/get", "角色详情查询"),
    SYSTEM_API_ROLE_CREATE("/system/api/role/create", "新建角色"),
    SYSTEM_API_ROLE_UPDATE("/system/api/role/update", "修改角色信息"),
    SYSTEM_API_ROLE_DISABLE("/system/api/role/disable", "停用角色"),
    SYSTEM_API_ROLE_ENABLE("/system/api/role/enable", "启用角色"),
    SYSTEM_API_ROLE_DELETE("/system/api/role/delete", "删除角色"),
    SYSTEM_API_ROLE_USER_LIST("/system/api/role/user/list", "角色用户配置查询"),
    SYSTEM_API_ROLE_USER_SAVE("/system/api/role/user/save", "角色用户配置修改"),
    SYSTEM_API_ROLE_PERMISSION_LIST("/system/api/role/permission/list", "角色应用权限配置查询"),
    SYSTEM_API_ROLE_PERMISSION_SAVE("/system/api/role/permission/save", "角色应用权限配置修改"),

    SYSTEM_APPS("/system/apps", "应用管理"),
    SYSTEM_API_APP_LIST("/system/api/app/list", "应用列表查询"),
    SYSTEM_API_APP_GET("/system/api/app/get", "应用详情查询"),
    SYSTEM_API_APP_CREATE("/system/api/app/create", "新建应用"),
    SYSTEM_API_APP_UPDATE("/system/api/app/update", "修改应用信息"),
    SYSTEM_API_APP_DISABLE("/system/api/app/disable", "停用应用"),
    SYSTEM_API_APP_ENABLE("/system/api/app/enable", "启用应用"),
    SYSTEM_API_APP_DELETE("/system/api/app/delete", "删除应用"),
    SYSTEM_API_APP_SECRET_GET("/system/api/app/secret/get", "查看应用密钥"),

    SYSTEM_PAGES("/system/pages", "页面管理"),
    SYSTEM_PAGE_EDIT("/system/page/edit", "编辑页面"),
    SYSTEM_API_PAGE_LIST("/system/api/page/list", "页面列表查询"),
    SYSTEM_API_PAGE_CREATE("/system/api/page/create", "新建页面"),
    SYSTEM_API_PAGE_BODY_GET("/system/api/page/body/get", "查看页面内容"),
    SYSTEM_API_PAGE_BODY_UPDATE("/system/api/page/body/update", "修改页面内容"),
    SYSTEM_API_PAGE_BODY_HISTORY_LIST("/system/api/page/body/history/list", "查看页面历史版本"),
    SYSTEM_API_PAGE_BODY_HISTORY_UPDATE("/system/api/page/body/history/update", "查看页面历史版本"),

    SYSTEM_API_PAGE_CONFIG_GET("/system/api/page/config/get", "查看页面配置"),
    SYSTEM_API_PAGE_CONFIG_UPDATE("/system/api/page/config/update", "修改页面配置"),
    SYSTEM_API_PAGE_ENABLE("/system/api/page/enable", "启用页面"),
    SYSTEM_API_PAGE_DISABLE("/system/api/page/disable", "停用页面"),
    SYSTEM_API_PAGE_DELETE("/system/api/page/delete", "删除页面"),

    SYSTEM_PERMISSIONS("/system/permissions", "权限管理"),
    SYSTEM_API_PERMISSION_LIST("/system/api/permission/list", "权限列表查询"),
    SYSTEM_API_PERMISSION_GET("/system/api/permission/get", "权限详情查询"),
    SYSTEM_API_PERMISSION_CREATE("/system/api/permission/create", "新建权限"),
    SYSTEM_API_PERMISSION_UPDATE("/system/api/permission/update", "修改权限信息"),
    SYSTEM_API_PERMISSION_DISABLE("/system/api/permission/disable", "停用权限"),
    SYSTEM_API_PERMISSION_ENABLE("/system/api/permission/enable", "启用权限"),
    SYSTEM_API_PERMISSION_DELETE("/system/api/permission/delete", "删除权限"),
    SYSTEM_API_PERMISSION_ROLE_LIST("/system/api/permission/role/list", "角色应用权限配置查询"),
    SYSTEM_API_PERMISSION_ROLE_SAVE("/system/api/permission/role/save", "角色应用权限配置修改"),

    SYSTEM_MENUS("/system/menus", "菜单管理"),
    SYSTEM_API_MENU_EDIT_GET("/system/api/menu/edit/get", "菜单编辑查询"),
    SYSTEM_API_MENU_EDIT_SAVE("/system/api/menu/edit/save", "菜单编辑保存"),

    SYSTEM_GROUPS("/system/groups", "项目组管理"),
    SYSTEM_API_GROUP_LIST("/system/api/group/list", "项目组列表查询"),
    SYSTEM_API_GROUP_GET("/system/api/group/get", "项目组详情查询"),
    SYSTEM_API_GROUP_CREATE("/system/api/group/create", "新建项目组"),
    SYSTEM_API_GROUP_UPDATE("/system/api/group/update", "修改项目组信息"),
    ;

    private final String sign;
    private final String name;

    public String getName() {
        return name;
    }

    SystemPermissions(String sign, String name) {
        this.sign = sign;
        this.name = name;
    }

    public Permission toPermission(Integer groupId) {
        return Permission.build(StringUtils.replace(sign, "{{groupId}}", String.valueOf(groupId)));
    }

}
