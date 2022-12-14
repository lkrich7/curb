package curb.server.vo;

import curb.server.po.PageBodyPO;

/**
 * 页面基本信息编辑对象
 */
public class PageBodyEditVO {

    /**
     * 页面ID
     */
    private Integer pageId;

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

    public PageBodyPO toPo() {
        PageBodyPO ret = new PageBodyPO();
        ret.setPageId(pageId);
        ret.setVersion(version);
        ret.setBody(body);
        return ret;
    }
}
