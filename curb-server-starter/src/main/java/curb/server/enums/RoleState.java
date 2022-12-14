package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 角色数据状态枚举
 */
public enum RoleState implements CodedEnum<RoleState> {
    DISABLED(0, "已禁用"),
    ENABLED(1, "已启用"),
    UNKNOWN(-1, "未知"),
    ;

    private final int code;
    private final String name;

    RoleState(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RoleState check(Integer roleState) {
        return CodedEnum.valueOfCode(RoleState.class, roleState, UNKNOWN);
    }
}
