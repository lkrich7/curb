package curb.server.enums;


import curb.core.CodedEnum;

/**
 * 用户操作类型枚举
 */
public enum OpType implements CodedEnum<OpType> {
    USER_ADD(11, "添加用户"),
    USER_DELETE(12, "删除用户"),
    ROLE_CREATE(23, "创建角色"),
    ROLE_DELETE(24, "删除角色"),
    APP_CREATE(35, "创建应用"),
    APP_DELETE(36, "删除应用"),
    APP_UPDATE(37, "修改应用"),
    PERMISSION_ADD(47, "添加权限"),
    PERMISSION_DELETE(48, "删除权限"),
    PERMISSION_UPDATE(49, "修改权限"),
    USER_ADD_ROLE(59, "用户添加角色"),
    USER_DELETE_ROLE(50, "用户删除角色"),
    ROLE_ADD_PERMISSION(51, "角色添加权限"),
    ROLE_DELETE_PERMISSION(52, "角色删除权限"),
    ;

    private final int code;
    private final String name;

    OpType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static OpType valueOfCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OpType ret : OpType.values()) {
            if (code.intValue() == ret.getCode()) {
                return ret;
            }
        }
        return null;
    }
}
