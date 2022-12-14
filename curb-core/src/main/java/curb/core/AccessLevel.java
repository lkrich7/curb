package curb.core;

/**
 * 访问控制级别枚举
 */
public enum AccessLevel {
    /**
     * 允许匿名访问
     */
    ANONYMOUS(0),
    /**
     * 允许所有登录用户访问
     */
    LOGIN(1),
    /**
     * 允许有权限用户访问
     */
    PERMISSION(2),
    ;

    private final int code;

    AccessLevel(int code) {
        this.code = code;
    }

    public static AccessLevel valueOfCode(Integer code) {
        return valueOfCode(code, null);
    }

    public static AccessLevel valueOfCode(Integer code, AccessLevel defaultValue) {
        if (code == null) {
            return defaultValue;
        }
        for (AccessLevel ret : AccessLevel.values()) {
            if (code == ret.getCode()) {
                return ret;
            }
        }
        return defaultValue;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "AccessLevel{" +
                "code=" + code +
                '}';
    }
}
