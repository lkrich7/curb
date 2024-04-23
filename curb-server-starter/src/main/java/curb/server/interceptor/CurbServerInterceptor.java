package curb.server.interceptor;

import curb.core.CurbAccessConfig;
import curb.core.CurbDataProvider;
import curb.core.configuration.CurbProperties;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.server.page.CurbPageRequestHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Curb服务器拦截器
 */
public class CurbServerInterceptor extends CurbInterceptor {

    public CurbServerInterceptor(CurbDataProvider dataProvider, CurbProperties properties) {
        super(dataProvider, properties);
    }

    @Override
    protected CurbAccessConfig resolveAccessConfig(HttpServletRequest request, Object handler) {
        if (handler instanceof CurbPageRequestHandler) {
            return ((CurbPageRequestHandler) handler).resolveAccessConfig(request);
        }
        return super.resolveAccessConfig(request, handler);
    }

}
