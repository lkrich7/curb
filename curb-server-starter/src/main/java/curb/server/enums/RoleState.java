package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 角色数据状态枚举
 */
public enum RoleState implements CodedEnum<RoleState> {
    UNKNOWN(0, "未知"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已停用"),
    ;

    private final int code;
    private final String name;

    RoleState(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return stringify();
    }

    @Override
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
