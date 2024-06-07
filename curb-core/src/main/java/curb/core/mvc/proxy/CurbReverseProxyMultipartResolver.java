package curb.core.mvc.proxy;

import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * 反向代理转发multipart请求，避免将原始HttpServletRequest 转为 MultipartHttpServletRequest，而造成无法转发的问题
 * 将StandardServletMultipartResolver进行包装，只有非代理路由的请求才会进行multipart解析
 */
public class CurbReverseProxyMultipartResolver extends StandardServletMultipartResolver {

    private final CurbReverseProxyHandlerMapping curbReverseProxyHandlerMapping;

    public CurbReverseProxyMultipartResolver(CurbReverseProxyHandlerMapping curbReverseProxyHandlerMapping) {
        this.curbReverseProxyHandlerMapping = curbReverseProxyHandlerMapping;
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        // 只有非代理路由的请求才会进行multipart解析
        return super.isMultipart(request) && !curbReverseProxyHandlerMapping.isMatched(request);
    }

}
