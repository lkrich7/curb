package curb.core;

/**
 * Curb框架自定义异常
 */
public class CurbException extends RuntimeException implements StatusMsg, ToApiResult {

    /**
     * 状态代码
     */
    private final int status;
    /**
     * 错误信息
     */
    private final String msg;

    public CurbException(StatusMsg statusMsg) {
        this(statusMsg.getStatus(), statusMsg.getMsg());
    }

    public CurbException(StatusMsg statusMsg, Throwable cause) {
        this(statusMsg.getStatus(), statusMsg.getMsg(), cause);
    }

    public CurbException(int status, String msg) {
        super(msg);
        this.status = status;
        this.msg = msg;
    }

    public CurbException(int status, String msg, Throwable cause) {
        super(msg, cause);
        this.status = status;
        this.msg = msg;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return "CurbException{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
