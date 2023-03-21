package curb.server.po;

import java.util.Date;

/**
 * 应用基本信息表(curb_app)数据持久化类
 */
public class AppPO {
    /**
     * 应用ID(主键)
     */
    private Integer appId;
    /**
     * 所属项目组ID
     */
    private Integer groupId;
    /**
     * 应用名称
     */
    private String name;
    /**
     * 应用网址
     */
    private String url;
    /**
     * 数据状态
     * @see curb.server.enums.AppState
     */
    private Integer state;
    /**
     * 数据创建时间
     */
    private Date createTime;
    /**
     * 数据最后更新时间
     */
    private Date updateTime;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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
        return "AppPO{" +
                "appId=" + appId +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
