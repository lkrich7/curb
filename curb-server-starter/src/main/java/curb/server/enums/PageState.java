package curb.server.enums;

import curb.core.CodedEnum;

/**
 * 页面数据状态枚举
 */
public enum PageState implements CodedEnum<PageState> {
    DISABLED(0, "未启用"),
    ENABLED(1, "启用"),
    ;

    private final int code;
    private final String name;

    PageState(int code, String name) {
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
