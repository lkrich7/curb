package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 应用数据状态枚举
 */
public enum AppState implements CodedEnum<AppState> {
    UNKNOWN(0, "未知"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已禁用"),
    ;

    private final int code;
    private final String name;

    AppState(int code, String name) {
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

    public static AppState check(Integer stateCode) {
        return CodedEnum.valueOfCode(AppState.class, stateCode, UNKNOWN);
    }
}
