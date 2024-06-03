package curb.core;

import curb.core.model.Permission;
import curb.core.util.StringUtil;
import curb.core.util.UrlCodec;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 默认请求权限对象解析器实现
 */
public class DefaultPermissionResolver implements PermissionResolver {

    @Override
    public Permission resolve(HttpServletRequest request, CurbAccessConfig config, Object handler) {
        String path = config.getPath();
        if (StringUtil.isBlank(path)) {
            path = request.getRequestURI();
        }
        Permission.Builder builder = Permission.builder().path(path);

        // 解析参数
        StringUtil.splitToStream(request.getQueryString(), "&")
                .map(StringUtil::trimToNull)
                .filter(Objects::nonNull)
                .forEach(s -> {
                    int index = s.indexOf('=');
                    if (index <= 0) {
                        return;
                    }
                    String key = s.substring(0, index);
                    String value = UrlCodec.decodeUtf8(s.substring(index + 1));
                    builder.addParam(key, value);
                });
        return builder.build();
    }

}
