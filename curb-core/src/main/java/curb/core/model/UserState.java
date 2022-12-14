package curb.core.model;

import curb.core.CodedEnum;

/**
 * 用户状态枚举
 */
public enum UserState implements CodedEnum<UserState> {

    /**
     * 正常
     */
    OK(1, "正常"),

    /**
     * 已停用
     */
    BLOCKED(0, "已停用"),

    /**
     * 不存在
     */
    NOT_EXISTED(-1, "不存在"),
    ;

    private final int code;
    private final String name;

    UserState(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserState check(User user) {
        if (null == user) {
            return NOT_EXISTED;
        }
        return CodedEnum.valueOfCode(UserState.class, user.getState(), NOT_EXISTED);
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isOk() {
        return this == OK;
    }

    @Override
    public String toString() {
        return "UserState-" + name() + "{" + "code=" + code + ", name='" + name + '\'' + '}';
    }
}
