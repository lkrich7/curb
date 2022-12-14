package curb.server.interceptor;

import curb.core.CurbAccessConfig;
import curb.core.ErrorEnum;
import curb.core.configuration.CurbProperties;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserState;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.server.page.CurbPageRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class CurbServerInterceptor extends CurbInterceptor {

    public CurbServerInterceptor() {
        super();
    }

    public CurbServerInterceptor(CurbProperties properties) {
        super(properties);
    }

    @Override
    protected boolean onUnauthenticated(User user, UserState userState, App app, Group group,
                                        HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (user == null) {
            throw ErrorEnum.NEED_LOGIN.toCurbException();
        }
        throw ErrorEnum.USER_BLOCKED.toCurbException();
    }

    @Override
    protected boolean onUnauthorized(User user, App app, Group group,
                                     HttpServletRequest request, HttpServletResponse response, Object handler) {
        throw ErrorEnum.FORBIDDEN.toCurbException();
    }

    @Override
    protected CurbAccessConfig resolveAccessConfig(HttpServletRequest request, Object handler) {
        if (handler instanceof CurbPageRequestHandler) {
            return ((CurbPageRequestHandler) handler).resolveAccessConfig(request);
        }
        return super.resolveAccessConfig(request, handler);
    }

}
