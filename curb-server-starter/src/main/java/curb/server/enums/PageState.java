package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 页面数据状态枚举
 */
public enum PageState implements CodedEnum<PageState> {
    UNKNOWN(0, "未知"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已停用"),
    ;

    private final int code;
    private final String name;

    PageState(int code, String name) {
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

    public static PageState check(Integer stateCode) {
        return CodedEnum.valueOfCode(PageState.class, stateCode, UNKNOWN);
    }
}
