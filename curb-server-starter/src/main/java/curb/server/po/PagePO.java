package curb.server.po;

import curb.server.vo.PageListItemVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 页面表(curb_page)数据持久化类
 */
public class PagePO {

    /**
     * 页面ID
     */
    private Integer pageId;

    /**
     * 所属应用ID
     */
    private Integer appId;

    /**
     * 页面路径
     */
    private String path;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面类型
     *
     * @see curb.server.enums.PageType
     */
    private Integer type;

    /**
     * 访问控制等级
     * @see curb.core.AccessLevel
     */
    private Integer accessLevel;

    /**
     * 访问权限串
     */
    private String sign;

    /**
     * 页面内容版本号
     */
    private Integer version;

    /**
     * 数据状态
     */
    private Integer state;

    /**
     * 页面创建用户
     */
    private Integer createUserId;

    /**
     * 最后更新用户
     */
    private Integer updateUserId;

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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
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
        return "PagePO{" +
                "pageId=" + pageId +
                ", appId=" + appId +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", pageType=" + type +
                ", version=" + version +
                ", accessLevel=" + accessLevel +
                ", sign='" + sign + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public PageListItemVO toPageListItemVO() {
        PageListItemVO pageListItemVO = new PageListItemVO();
        pageListItemVO.setPageId(this.getPageId());
        pageListItemVO.setName(this.getName());
        pageListItemVO.setPath(this.getPath());
        pageListItemVO.setType(this.getType());
        pageListItemVO.setAccessLevel(this.getAccessLevel());
        pageListItemVO.setState(this.getState());
        pageListItemVO.setUpdateTime(this.getUpdateTime());
        return pageListItemVO;
    }

    public static PageListItemVO convertToPageListItemVO(PagePO pagePO) {
        if (pagePO == null) {
            return null;
        }
        return pagePO.toPageListItemVO();
    }

}
