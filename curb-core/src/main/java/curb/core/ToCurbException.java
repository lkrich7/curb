package curb.core;

/**
 * 扩展 StatusMsg 接口，提供转为 CurbException 的默认方法
 */
public interface ToCurbException extends StatusMsg {

    default CurbException toCurbException() {
        return new CurbException(this);
    }

    default CurbException toCurbException(String msg) {
        return new CurbException(getStatus(), msg);
    }

    default CurbException toCurbException(Throwable cause) {
        return new CurbException(this, cause);
    }

    default CurbException toCurbException(String msg, Throwable cause) {
        return new CurbException(getStatus(), msg, cause);
    }
}
