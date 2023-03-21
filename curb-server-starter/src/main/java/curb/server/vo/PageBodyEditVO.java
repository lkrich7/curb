package curb.server.vo;

import curb.server.po.PageBodyPO;

/**
 * 页面基本信息编辑对象
 */
public class PageBodyEditVO {


    /**
     * 页面ID`
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
     * 页面内容版本号
     */
    private Integer version;

    /**
     * 页面内容
     */
    private String body;

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

    @Override
    public String toString() {
        return "PageBodyEditVO{" +
                "pageId=" + pageId +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", version=" + version +
                ", body='" + body + '\'' +
                '}';
    }
}
