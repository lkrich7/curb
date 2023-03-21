package curb.server.page;

import curb.core.CurbException;
import curb.core.ErrorEnum;
import curb.core.model.Group;
import curb.core.util.CurbUtil;
import curb.server.util.CurbServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public class CurbPageHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbPageHandlerExceptionResolver.class);

    private int order;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        String errorMessage;
        HttpStatus httpStatus;
        if (e instanceof CurbException) {
            CurbException curbEx = (CurbException) e;
            if (ErrorEnum.NEED_LOGIN.statusEquals(curbEx.getStatus())) {
                return redirectToLogin(request);
            }
            errorMessage = curbEx.getMsg();
            httpStatus = toHttpStatus(curbEx.getStatus());
        } else {
            errorMessage = ErrorEnum.SERVER_ERROR.getMsg();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String url = CurbServerUtil.getUrl(request);
        if (httpStatus.is5xxServerError()) {
            LOGGER.error("resolved exception [{}] : {}", url, e.getMessage(), e);
        } else {
            LOGGER.info("resolved exception [{}] : {}", url, e.getMessage());
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("errorMessage", errorMessage);
        return new ModelAndView("/error", modelMap, httpStatus);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private ModelAndView redirectToLogin(HttpServletRequest request) {
        Group group = CurbUtil.getGroup(request);
        String redirectUrl = CurbServerUtil.buildLoginUrl(request, group.getUrl());
        return new ModelAndView("redirect:" + redirectUrl);
    }

    private HttpStatus toHttpStatus(int code) {
        if (ErrorEnum.USER_BLOCKED.getStatus() == code) {
            return HttpStatus.FORBIDDEN;
        }
        HttpStatus ret = HttpStatus.resolve(code);
        return ret == null ? HttpStatus.INTERNAL_SERVER_ERROR : ret;
    }
}
