package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 权限数据状态枚举
 */
public enum PermissionState implements CodedEnum<PermissionState> {
    UNKNOWN(0, "未知"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已停用"),
    ;

    private final int code;
    private final String name;

    PermissionState(int code, String name) {
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

    public static PermissionState check(Integer stateCode) {
        return CodedEnum.valueOfCode(PermissionState.class, stateCode, UNKNOWN);
    }
}
