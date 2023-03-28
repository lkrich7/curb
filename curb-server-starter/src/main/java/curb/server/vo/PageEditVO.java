package curb.server.vo;

/**
 * 页面基本信息编辑对象
 */
public class PageEditVO {

    /**
     * 页面ID
     */
    private Integer pageId;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面路径
     */
    private String path;

    /**
     * 页面类型
     *
     * @see curb.server.enums.PageType
     */
    private Integer type;

    /**
     * 访问控制等级
     *
     * @see curb.core.AccessLevel
     */
    private Integer accessLevel;

    /**
     * 访问权限串
     */
    private String sign;

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
