package curb.demo;

import curb.core.CurbAccessConfig;
import curb.core.CurbException;
import curb.core.ErrorEnum;
import curb.core.PermissionResolver;
import curb.core.model.Permission;
import curb.core.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 自定义权限解析器
 * 将带有@RequestBody 的请求解析成权限对象
 *
 * @author liukan
 */
@Component
public class DemoPermissionResolver implements PermissionResolver {

    @Override
    public Permission resolve(HttpServletRequest request, CurbAccessConfig config, Object handler) {
        String path = config.getPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        Permission.Builder builder = Permission.builder().path(path);

        Class<?> clazz = getClass(handler);

        try (InputStream is = request.getInputStream()) {
//            Object body = JsonUtl.parseObject(is, StandardCharsets.UTF_8, clazz);
            Object body = null;

            if (body instanceof DemoRequestBody) {
                DemoRequestBody requestBody = (DemoRequestBody) body;
                builder.addParam("name", requestBody.getName());
                return builder.build();
            }
            if (body instanceof DemoComplexRequestBody) {
                DemoComplexRequestBody requestBody = (DemoComplexRequestBody) body;
                builder.addParams("agentIds", StringUtils.split(requestBody.getAgentIds(), ','));
                builder.addParams("dspIds", StringUtils.split(requestBody.getDspIds(), ','));
                builder.addParam("myId", requestBody.getMyId());
                return builder.build();
            }

            return builder.build();
        } catch (Exception e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    private Class<?> getClass(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return null;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        for (MethodParameter parameter : parameters) {
            if (parameter.hasParameterAnnotation(RequestBody.class)) {
                return parameter.getParameterType();
            }
        }
        return null;
    }

}
