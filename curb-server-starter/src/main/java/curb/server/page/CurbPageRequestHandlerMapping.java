package curb.server.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Curb 托管页面的请求处理器映射类
 *
 * @see org.springframework.web.servlet.HandlerMapping
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping
 */
public class CurbPageRequestHandlerMapping extends AbstractHandlerMapping {

    private final CurbPageRequestHandler handler;

    public CurbPageRequestHandlerMapping(CurbPageRequestHandler handler) {
        this.handler = handler;
    }
    @Override
    protected Object getHandlerInternal(HttpServletRequest request) {
        return handler.checkHandler(request);
    }

}
