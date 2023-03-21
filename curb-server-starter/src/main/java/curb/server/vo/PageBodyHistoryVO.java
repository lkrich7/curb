package curb.server.vo;

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
     * 提交用户ID
     */
    private Integer userId;

    /**
     * 修改时间
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
