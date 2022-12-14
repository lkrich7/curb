package curb.server.api.advice;

import curb.core.ApiResult;
import curb.core.CurbException;
import curb.core.ErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Curb API Controller 的异常异常处理器
 */
@ControllerAdvice({"curb.server.api.controller"})
public class CurbApiControllerAdvice {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CurbApiControllerAdvice.class);

    private String buildErrMsg(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod()).append(' ').append(request.getRequestURI());
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            builder.append('?').append(query);
        }
        return builder.toString();
    }

    @ResponseBody
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            TypeMismatchException.class,
            ConversionFailedException.class,
            MultipartException.class,
    })
    public ApiResult<Object> handleParamException(Throwable e, HttpServletRequest request,
                                                  HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        LOGGER.error("handleParamException {}:{}", msg, e.getMessage(), e);
        return ErrorEnum.PARAM_ERROR.toApiResult();
    }

    @ResponseBody
    @ExceptionHandler(CurbException.class)
    public ApiResult<Object> handleCurbException(CurbException e, HttpServletRequest request,
                                                 HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        LOGGER.info("handleCurbException {}:{}:{}", msg, e.getMsg(), e.getMessage());
        return e.toApiResult();
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ApiResult<Object> handleException(Throwable e, HttpServletRequest request,
                                             HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        LOGGER.error("handleException {} : {}", msg, e.getMessage(), e);
        return ErrorEnum.SERVER_ERROR.toApiResult();
    }
}
