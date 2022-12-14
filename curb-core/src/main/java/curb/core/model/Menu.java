package curb.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单数据
 */
public class Menu implements Serializable {

    /**
     * 菜单项名称
     */
    private String name;

    /**
     * 菜单项URL
     */
    private String url;

    /**
     * 菜单项URL类型
     */
    private int urlType;

    /**
     * 是否跳过权限检查
     */
    private boolean skipCheck;

    /**
     * 菜单项图标
     */
    private String icon;

    /**
     * 子菜单项
     */
    private List<Menu> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    public boolean isSkipCheck() {
        return skipCheck;
    }

    public void setSkipCheck(boolean skipCheck) {
        this.skipCheck = skipCheck;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", urlType=" + urlType +
                ", icon='" + icon + '\'' +
                ", checkPermission=" + skipCheck +
                ", children=" + children +
                '}';
    }
}
