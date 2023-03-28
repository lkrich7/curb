package curb.server.vo;

import curb.core.model.User;

import java.util.Date;

public class PageBodyHistoryVO {

    /**
     * 页面ID
     */
    private Integer pageId;

    /**
     * 页面内容版本号
     */
    private Integer version;

    /**
     * 提交用户
     */
    private User user;

    /**
     * 提交时间
     */
    private Date updateTime;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
