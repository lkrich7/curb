package curb.server.advice;

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
 *
 */
@ControllerAdvice({"curb.server.controller"})
public class SystemApiControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemApiControllerAdvice.class);

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
    public ApiResult<Void> handleParamException(Throwable e, HttpServletRequest request,
                                                HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        LOGGER.error("handleParamException {}:{}", msg, e.getMessage(), e);
        return ErrorEnum.PARAM_ERROR.toApiResult();
    }

    @ResponseBody
    @ExceptionHandler(CurbException.class)
    public ApiResult<Void> handleCurbException(CurbException e, HttpServletRequest request,
                                               HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        if (ErrorEnum.SERVER_ERROR.getStatus() == e.getStatus()) {
            LOGGER.error("handleCurbException {}:{}", msg, e.getMessage(), e);
        } else {
            LOGGER.info("handleCurbException {}:{}", msg, e.getMessage());
        }
        return e.toApiResult();
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ApiResult<Void> handleException(Throwable e, HttpServletRequest request,
                                           HttpServletResponse response) {
        String msg = buildErrMsg(request, response);
        LOGGER.error("handleException {}:{}", msg, e.getMessage(), e);
        return ErrorEnum.SERVER_ERROR.toApiResult(null, e.getMessage());
    }
}
