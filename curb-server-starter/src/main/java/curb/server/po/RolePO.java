package curb.server.po;

import java.util.Date;

/**
 * 角色表(curb_role)数据持久化类
 */
public class RolePO {
    /**
     * 角色ID(主键)
     */
    private Integer roleId;
    /**
     * 所属项目组ID
     */
    private Integer groupId;
    /**
     * 角色标识
     */
    private String sign;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色说明
     */
    private String description;
    /**
     * 数据状态
     * @see curb.server.enums.RoleState
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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
        return "RolePO{" +
                "roleId=" + roleId +
                ", groupId=" + groupId +
                ", sign='" + sign + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
