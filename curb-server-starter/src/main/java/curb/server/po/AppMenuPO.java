package curb.server.po;

import java.util.Date;

/**
 * 应用菜单(curb_app_menu)数据持久化类
 */
public class AppMenuPO {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 应用菜单数据版本号
     */
    private Integer version;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 菜单内容
     */
    private String menu;

    /**
     * 数据创建时间
     */
    private Date createTime;

    /**
     * 数据更新时间
     */
    private Date updateTime;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
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

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
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
        return "AppMenuPO{" +
                "appId=" + appId +
                ", version=" + version +
                ", userId=" + userId +
                ", menu='" + menu + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
