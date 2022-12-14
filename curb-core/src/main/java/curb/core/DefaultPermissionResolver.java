package curb.core;

import curb.core.model.Permission;
import curb.core.util.UrlCodec;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 默认请求权限对象解析器实现
 */
public class DefaultPermissionResolver implements PermissionResolver {

    @Override
    public Permission resolve(HttpServletRequest request, CurbAccessConfig config, Object handler) {
        String path = config.getPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        Permission.Builder builder = Permission.builder().path(path);

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (String value : values) {
                    builder.addParam(name, UrlCodec.encodeUtf8(value));
                }
            }
        }

        return builder.build();
    }

}
