package curb.core;

import java.io.Serializable;

/**
 * API返回结果
 *
 * @param <D> data字段数据类型
 */
public class ApiResult<D> implements StatusMsg, Serializable {

    /**
     * 状态值 0表示成功
     */
    private int status;

    /**
     * 错误消息
     */
    private String msg;

    /**
     * 返回的数据
     */
    private D data;

    public ApiResult(StatusMsg statusMsg) {
        this(statusMsg, null);
    }

    public ApiResult(StatusMsg statusMsg, D data) {
        this(statusMsg.getStatus(), statusMsg.getMsg(), data);
    }

    public ApiResult(int status, String msg, D data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
