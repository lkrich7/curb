package curb.server.util;

import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.util.ServletUtil;
import curb.core.util.UrlCodec;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.UUID;

/**
 *
 */
public final class CurbServerUtil {

    public static final int CURB_APP_ID = 1;
    public static final int CURB_GROUP_ID = 1;

    private static final String ATTRIBUTE_NAME_GROUP_SECRET = "curbGroupSecret";

    private CurbServerUtil() {
    }

    /**
     * 判断当前应用是否是项目组主应用
     *
     * @param group
     * @param app
     * @return
     */
    public static boolean isGroupApp(Group group, App app) {
        return isSystemAppId(app.getAppId()) || group.getUrl().equals(app.getUrl());
    }

    /**
     * 判断是否为系统应用ID
     *
     * @param appId
     * @return
     */
    public static boolean isSystemAppId(int appId) {
        return CURB_APP_ID == appId;
    }

    /**
     * 判断是否为系统项目组ID
     *
     * @param groupId
     * @return
     */
    public static boolean isSystemGroupId(int groupId) {
        return CURB_GROUP_ID == groupId;
    }

    public static Integer checkGroupId(Integer groupId, Group group) {
        if (groupId == null) {
            groupId = group.getGroupId();
        } else {
            if (!isSystemGroupId(group.getGroupId()) && !groupId.equals(group.getGroupId())) {
                ErrorEnum.NOT_FOUND.toCurbException();
            }
        }
        return groupId;
    }

    public static String generateSecret() {
        return UUID.randomUUID().toString();
    }

    public static String getGroupSecret(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_GROUP_SECRET);
    }

    public static String buildLoginUrl(HttpServletRequest request, URI url) {
        String targetUrl = getUrl(request);
        String targetUrlEncoded = UrlCodec.encodeUtf8(targetUrl);
        return String.format("%s/login?targetUrl=%s", url, targetUrlEncoded);
    }

    public static String getUrl(HttpServletRequest request) {
        String scheme = getScheme(request);
        String domain = getDomain(request);
        String path = request.getRequestURI();
        String query = StringUtils.trimToNull(request.getQueryString());
        StringBuilder builder = new StringBuilder(scheme)
                .append("://")
                .append(domain)
                .append(path);
        if (query != null) {
            builder.append("?").append(query);
        }
        return builder.toString();
    }


    /**
     * 返回客户端请求的网络协议名
     *
     * @param request
     * @return
     */
    private static String getScheme(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.trim().isEmpty()) {
            scheme = request.getScheme();
        }
        return scheme.trim().toLowerCase();
    }

    /**
     * 返回客户端请求的域
     *
     * @param request
     * @return
     */
    private static String getDomain(HttpServletRequest request) {
        String domain = request.getHeader("X-CURB-HOST");
        if (StringUtils.isNotBlank(domain)) {
            return domain;
        }
        domain = request.getHeader("Host");
        if (StringUtils.isNotBlank(domain)) {
            return domain;
        }
        int port = request.getServerPort();
        String scheme = getScheme(request);
        domain = buildDomain(request.getServerName(), port, scheme);
        return domain;
    }

    /**
     * 根据主机名、端口号和协议名构造域
     *
     * @param host
     * @param port
     * @param scheme
     * @return
     */
    private static String buildDomain(String host, int port, String scheme) {
        if (("http".equalsIgnoreCase(scheme) && port != 80)
                || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            return host + ":" + port;
        }
        return host;
    }

    /**
     * 判断一个域名是否和另一个域名相同或是另一个域名的子域名
     *
     * @param subDomain
     * @param domain
     * @return
     */
    private static boolean isEqualOrSub(String subDomain, String domain) {
        if (!subDomain.endsWith(domain)) {
            return false;
        }
        int i = subDomain.length() - domain.length();
        if (i < 0) {
            return false;
        }
        if (i == 0) {
            return true;
        }
        return subDomain.charAt(i - 1) == '.';
    }

}
