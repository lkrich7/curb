package curb.server.po;

import curb.server.vo.PageBodyHistoryVO;

import java.util.Date;

/**
 * 页面表(curb_page)数据持久化类
 */
public class PageBodyPO {

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

    /**
     * 提交用户ID
     */
    private Integer userId;

    /**
     * 数据创建时间
     */
    private Date createTime;
    /**
     * 数据更新时间
     */
    private Date updateTime;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "PageBodyPO{" +
                "pageId=" + pageId +
                ", version=" + version +
                ", body='" + body + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public PageBodyHistoryVO toVO() {
        PageBodyHistoryVO ret = new PageBodyHistoryVO();
        ret.setPageId(pageId);
        ret.setVersion(version);
        ret.setUserId(userId);
        ret.setUpdateTime(updateTime);
        return ret;
    }
}
