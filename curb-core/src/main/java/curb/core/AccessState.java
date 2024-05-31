package curb.core;

public enum AccessState {
    ANONYMOUS(true),
    USER_NOT_EXISTED(false),
    USER_BLOCKED(false),
    LOGIN(true),
    AUTHORIZED(true),
    UNAUTHORIZED(false),
    ;

    private final boolean passed;

    AccessState(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", passed ? "Passed" : "Denied", name());
    }
}
