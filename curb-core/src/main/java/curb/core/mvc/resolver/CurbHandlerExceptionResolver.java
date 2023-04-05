package curb.core.mvc.resolver;

import curb.core.CurbException;
import curb.core.ErrorEnum;
import curb.core.model.Group;
import curb.core.util.CurbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CurbHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbHandlerExceptionResolver.class);

    private static boolean shouldReturnJson(HttpServletRequest request, Object handler) {
        boolean returnJson = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            returnJson = handlerMethod.hasMethodAnnotation(ResponseBody.class);
            if (!returnJson) {
                returnJson = handlerMethod.getBeanType().isAnnotationPresent(RestController.class);
            }
        }
        return returnJson;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        if (!(e instanceof CurbException)) {
            return null;
        }
        CurbException curbEx = (CurbException) e;

        ModelAndView ret;
        if (shouldReturnJson(request, handler)) {
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            view.setUpdateContentLength(true);
            ret = new ModelAndView(view);
            ret.addObject("status", curbEx.getStatus());
            ret.addObject("msg", curbEx.getMsg());
            return ret;
        }
        if (ErrorEnum.NEED_LOGIN.statusEquals(curbEx.getStatus())) {
            return redirectToLogin(request);
        }
        HttpStatus httpStatus;
        httpStatus = toHttpStatus(curbEx.getStatus());
        ret = new ModelAndView("/error", httpStatus);
        ret.addObject("errorMessage", curbEx.getMsg());
        return ret;
    }

    private ModelAndView redirectToLogin(HttpServletRequest request) {
        Group group = CurbUtil.getGroup(request);
        String redirectUrl = CurbUtil.buildLoginUrl(request, group.getUrl());
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
