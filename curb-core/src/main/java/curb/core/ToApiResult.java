package curb.core;

/**
 * 提供默认方法将 StatusMsg 转为 ApiResult
 */
public interface ToApiResult extends StatusMsg {

    default <D> ApiResult<D> toApiResult() {
        return new ApiResult<>(this, null);
    }

    default <D> ApiResult<D> toApiResult(D data) {
        return new ApiResult<>(this, data);
    }

    default <D> ApiResult<D> toApiResult(D data, String msg) {
        return new ApiResult<>(getStatus(), msg, data);
    }
}
