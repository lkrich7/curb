package curb.server.page;

import curb.core.AccessLevel;
import curb.core.CurbAccessConfig;
import curb.core.util.JsonUtil;
import curb.server.enums.PageType;
import curb.server.po.PageBodyPO;
import curb.server.po.PagePO;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Curb 页面模型
 */
public class CurbPage implements CurbAccessConfig, Serializable {

    public static final CurbPage INDEX;
    private static final String WRAP_PAGE_PREFIX = "/page-wrap";
    private static final String VIEW_PAGE_PREFIX = "/page-view";
    private static final String PATH_PATTERN_STR = "(/(?!/)|(?:/[\\w._\\-]+)+(?=/?))(?:/?\\?(.*))?";
    private static final Pattern PAGE_PATH_PATTERN = Pattern.compile("^" + PATH_PATTERN_STR + "$");
    private static final Pattern WRAP_PAGE_PATH_PATTERN = Pattern.compile("^(?:" + WRAP_PAGE_PREFIX + ")+" + PATH_PATTERN_STR + "$");
    private static final Pattern VIEW_PAGE_PATH_PATTERN = Pattern.compile("^(?:" + VIEW_PAGE_PREFIX + ")+" + PATH_PATTERN_STR + "$");

    static {
        INDEX = new CurbPage();
        INDEX.setPath("/");
        INDEX.setType(PageType.AMIS);
        INDEX.setName("首页");
    }

    /**
     * 页面地址路径(无参数)
     */
    private String path;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面类型
     *
     * @see PageType
     */
    private PageType type;

    /**
     * 页面访问控制等级
     */
    private AccessLevel level;

    /**
     * 页面使用的权限标识
     */
    private String sign;

    /**
     * 页面内容版本号
     */
    private Integer version;
    /**
     * 页面内容
     */
    private String body;

    public static CurbPage fromPO(PagePO po, PageBodyPO bodyPO) {
        if (po == null) {
            return null;
        }
        PageType type = PageType.valueOfCode(po.getType(), PageType.AMIS);
        AccessLevel level = AccessLevel.valueOfCode(po.getAccessLevel(), AccessLevel.PERMISSION);
        CurbPage ret = new CurbPage();
        ret.setPath(po.getPath());
        ret.setName(po.getName());
        ret.setType(type);
        ret.setLevel(level);
        ret.setSign(po.getSign());
        ret.setVersion(po.getVersion());
        if (bodyPO != null) {
            ret.setBody(bodyPO.getBody());
        }
        return ret;
    }

    public static CurbPage buildWrapPage(String url) {
        Matcher matcher = WRAP_PAGE_PATH_PATTERN.matcher(url);
        if (matcher.matches()) {
            String path = matcher.group(1);
            String query = matcher.group(2);
            String src = query == null || query.isEmpty() ? path : path + "?" + query;
            Map<String, Object> schema = new LinkedHashMap<>();
            schema.put("type", "iframe");
            schema.put("src", src);
            schema.put("height", "768px");
            CurbPage ret = new CurbPage();
            ret.setPath(path);
            ret.setName(src);
            ret.setType(PageType.AMIS);
            ret.setBody(JsonUtil.toJSONString(schema));
            return ret;
        }
        return null;
    }

    public static String stripViewPagePath(String pathWithQuery) {
        Matcher matcher = VIEW_PAGE_PATH_PATTERN.matcher(pathWithQuery);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static final String parsePath(String pathWithQuery) {
        Matcher matcher = PAGE_PATH_PATTERN.matcher(pathWithQuery);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isIndex(String path) {
        return "/".equals(path);
    }

    public static boolean isWrapPageUrl(String url) {
        if (url == null) {
            return false;
        }
        return WRAP_PAGE_PATH_PATTERN.matcher(url).matches();
    }

    public static boolean isViewPageUrl(String url) {
        if (url == null) {
            return false;
        }
        return VIEW_PAGE_PATH_PATTERN.matcher(url).matches();
    }

    public static String asWrapPage(String url) {
        return WRAP_PAGE_PREFIX + url;
    }

    public static String asViewPage(String url) {
        return VIEW_PAGE_PREFIX + url;
    }

    public Object toAmisSchema() {
        if (body == null || body.isEmpty()) {
            return null;
        }
        if (type == PageType.AMIS) {
            return JsonUtil.parseObject(body);
        }
        if (type == PageType.HTML) {
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("type", "html");
            ret.put("html", body);
            return ret;
        }
        return null;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }

    @Override
    public AccessLevel getLevel() {
        return level;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    @Override
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
