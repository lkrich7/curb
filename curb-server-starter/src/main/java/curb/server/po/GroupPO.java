package curb.server.po;

import java.util.Date;

/**
 * 项目组基本信息表
 */
public class GroupPO {
    /**
     * 项目组ID
     */
    private Integer groupId;
    /**
     * 项目组名称
     */
    private String name;
    /**
     * 项目组网址
     */
    private String url;
    /**
     * 数据创建时间
     */
    private Date createTime;
    /**
     * 数据最后更新时间
     */
    private Date updateTime;

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
}
