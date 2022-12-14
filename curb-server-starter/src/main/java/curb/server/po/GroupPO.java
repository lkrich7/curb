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
     * 项目组主域名
     */
    private String domain;
    /**
     * 项目组名称
     */
    private String name;
    /**
     * 项目组描述
     */
    private String description;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
