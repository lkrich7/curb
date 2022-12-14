package curb.server.po;

import java.util.Date;

/**
 * 操作日志
 */
public class OpLogPO {
    /**
     * 操作日志ID(主键)
     */
    private Long logId;
    /**
     * 操作用户ID
     */
    private Integer userId;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 操作标题
     */
    private String title;
    /**
     * 操作详情
     */
    private String content;
    /**
     * 操作时间
     */
    private Date opTime;
    /**
     * 数据创建时间
     */
    private Date createTime;
    /**
     * 数据更新时间
     */
    private Date updateTime;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
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
        return "OpLogPO{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", opTime=" + opTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}