package curb.server.api.interceptor;

import curb.core.util.ApiSignUtil;
import curb.core.ErrorEnum;
import curb.server.po.AppPO;
import curb.server.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * API接口参数签名验证拦截器
 */
public class CurbApiInterceptor extends HandlerInterceptorAdapter {

    private static final long EXPIRE_MILLIS = 300L * 1000L;

    private static final String APP_ID_PARAM_NAME = "appId";

    private static final String TIMESTAMP_PARAM_NAME = "t";

    @Autowired
    private AppService appService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        int appId;
        try {
            appId = ServletRequestUtils.getRequiredIntParameter(request, APP_ID_PARAM_NAME);
        } catch (Exception e) {
            throw ErrorEnum.PARAM_ERROR.toCurbException();
        }
        long t;
        try {
            t = ServletRequestUtils.getRequiredLongParameter(request, TIMESTAMP_PARAM_NAME);
        } catch (Exception e) {
            throw ErrorEnum.PARAM_ERROR.toCurbException();
        }

        if (Math.abs(System.currentTimeMillis() - t) > EXPIRE_MILLIS) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("请求已过期，请重试");
        }

        AppPO app = appService.get(appId);
        if (app == null || app.getAppId() <= 0) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("应用不存在");
        }
        String appSecret = appService.getSecret(appId);

        boolean success = ApiSignUtil.checkSignMatched(request, appSecret);
        if (!success) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("apiToken 错误");
        }

        return true;
    }


}
