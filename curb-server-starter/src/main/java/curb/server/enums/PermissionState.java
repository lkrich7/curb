package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 权限数据状态枚举
 */
public enum PermissionState implements CodedEnum<PermissionState> {
    DISABLED(0, "未启用"),
    ENABLED(1, "启用"),
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
}
