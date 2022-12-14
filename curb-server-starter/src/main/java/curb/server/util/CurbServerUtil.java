package curb.server.util;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.util.ServletUtil;
import curb.core.util.UrlCodec;
import curb.core.ErrorEnum;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.UUID;

/**
 */
public final class CurbServerUtil {

    public static final int CURB_APP_ID = 1;
    public static final int CURB_GROUP_ID = 1;

    private static final String ATTRIBUTE_NAME_GROUP_SECRET = "curbGroupSecret";

    private CurbServerUtil() {
    }

    public static boolean isGroupApp(Group group, App app) {
        return isSystemAppId(app.getAppId()) || group.getDomain().equalsIgnoreCase(app.getDomain());
    }

    public static boolean isSystemAppId(int appId) {
        return CURB_APP_ID == appId;
    }

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
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getGroupSecret(HttpServletRequest request) {
        return ServletUtil.getObjectFromRequest(request, ATTRIBUTE_NAME_GROUP_SECRET);
    }

    public static String getDomain(HttpServletRequest request) {
        String domain = request.getHeader("X-CURB-HOST");
        if (StringUtils.isNotBlank(domain)) {
            return domain;
        }
        domain = request.getHeader("Host");
        if (StringUtils.isNotBlank(domain)) {
            return domain;
        }
        domain = request.getServerName() + ":" + request.getServerPort();              ;
        return domain;
    }

    public static String getScheme(HttpServletRequest request) {
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto == null || xForwardedProto.trim().isEmpty()) {
            return request.getScheme();
        }
        return xForwardedProto;
    }

    public static final String getUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();

        return buildUrl(request, path, queryString);
    }

    public static final String buildUrl(HttpServletRequest request, String path, String queryString) {
        StringBuilder url = buildUrlBase(request);
        if (!path.startsWith("/")) {
            url.append("/");
        }
        url.append(path);

        if (StringUtils.isNotBlank(queryString)) {
            url.append("?");
            url.append(queryString);
        }

        return url.toString();
    }

    private static final StringBuilder buildUrlBase(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();

        String scheme = request.getScheme();
        String domain = getDomain(request);
        int port = request.getServerPort();
        url.append(scheme);
        url.append("://");
        url.append(domain);
        if (("http".equalsIgnoreCase(scheme) && port != 80)
                || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            url.append(':');
            url.append(port);
        }
        return url;
    }

    public static String buildLoginUrl(HttpServletRequest request, String domain, String targetUrl) {
        String scheme = getScheme(request);
        targetUrl = checkTargetUrl(targetUrl, request);
        return buildLoginUrl(scheme, domain, targetUrl);
    }

    public static final String buildLoginUrl(HttpServletRequest request, String domain) {
        String scheme = getScheme(request);
        String currentDomain = getDomain(request);
        if (!isEqualOrSub(currentDomain, domain)) {
            domain = currentDomain;
        }
        String targetUrl = getUrl(request);
        return buildLoginUrl(scheme, domain, targetUrl);
    }


    private static final String buildLoginUrl(String scheme, String domain, String targetUrlSafe) {
        targetUrlSafe = UrlCodec.encodeUtf8(targetUrlSafe);
        return String.format("%s://%s/login?targetUrl=%s", scheme, domain, targetUrlSafe);
    }

    public static String checkTargetUrl(String targetUrl, HttpServletRequest request) {
        do try {
            String domain = CurbServerUtil.getDomain(request);
            URI targetUri = URI.create(targetUrl);
            String scheme = targetUri.getScheme();
            String targetDomain = targetUri.getHost();
            if (scheme != null && !"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                // 非HTTP(S)协议
                break;
            }
            if (targetDomain == null || targetDomain.isEmpty()) {
                // 无域名
                return CurbServerUtil.buildUrl(request, targetUrl, null);
            }
            if (isEqualOrSub(targetDomain, domain)) {
                // 同域或子域
                return targetUrl;
            }
        } catch (Exception e) {
            // ignored
        } while (false);
        return CurbServerUtil.buildUrl(request, "/", null);
    }

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
