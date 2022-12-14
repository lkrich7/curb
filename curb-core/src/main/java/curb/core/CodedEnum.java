package curb.core;

/**
 * 自定义代码值枚举增强接口
 *
 * @param <E> 枚举类型
 */
public interface CodedEnum<E extends Enum<E>> {

    /**
     * 获取代码值
     *
     * @return
     */
    int getCode();

    default boolean codeEquals(int code) {
        return code == getCode();
    }

    default boolean codeEquals(Integer code) {
        return code != null && code.intValue() == getCode();
    }

    static <E extends CodedEnum> E valueOfCode(Class<E> clazz, Integer code, E defaultValue) {
        if (code == null) {
            return defaultValue;
        }
        return valueOfCode(clazz, code.intValue(), defaultValue);
    }
    static <E extends CodedEnum> E valueOfCode(Class<E> clazz, int code, E defaultValue) {
        E[] values = clazz.getEnumConstants();
        for (E ret : values) {
            if (ret.codeEquals(code)) {
                return ret;
            }
        }
        return defaultValue;
    }

}
