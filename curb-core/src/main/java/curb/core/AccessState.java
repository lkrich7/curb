package curb.core;

public enum AccessState implements CodedEnum<AccessState> {
    ANONYMOUS(1, true),
    USER_NOT_EXISTED(2, false),
    USER_BLOCKED(3, false),
    LOGIN(4, true),
    AUTHORIZED(5, true),
    UNAUTHORIZED(6, false),
    ;

    private final int code;
    private final boolean passed;

    AccessState(int code, boolean passed) {
        this.code = code;
        this.passed = passed;
    }

    @Override
    public int getCode() {
        return code;
    }

    public boolean isPassed() {
        return passed;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", passed ? "Passed" : "Denied", name());
    }
}
