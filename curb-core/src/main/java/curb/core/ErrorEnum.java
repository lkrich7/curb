package curb.core;

/**
 * 统一错误信息枚举
 */
public enum ErrorEnum implements StatusMsg, ToApiResult, ToCurbException {
    SUCCESS(0, "成功"),
    PARAM_ERROR(400, "缺少请求参数或请求参数不合法"),
    NEED_LOGIN(401, "需要用户登录"),
    USER_BLOCKED(402, "您的账号已被停用"),
    FORBIDDEN(403, "您没有该操作权限"),
    NOT_FOUND(404, "请求的内容未找到"),
    SERVER_ERROR(500, "服务端错误"),
    ;

    private final int status;
    private final String msg;

    ErrorEnum(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static ErrorEnum get(int status) {
        for (ErrorEnum ret : ErrorEnum.values()) {
            if (status == ret.getStatus()) {
                return ret;
            }
        }
        return null;
    }

    public static ErrorEnum get(Integer status) {
        if (status == null) {
            return null;
        }
        return get(status.intValue());
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public boolean statusEquals(int status) {
        return this.status == status;
    }

    @Override
    public String toString() {
        return "ErrorEnum-" + name() + "{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
