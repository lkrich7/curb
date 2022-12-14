package curb.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * 应用相关详细信息聚合
 */
public class AppDetail implements Serializable {

    /**
     * 当前应用基本信息
     */
    private App app;

    /**
     * 当前应用菜单
     */
    private List<Menu> menu;

    /**
     * 应用所属项目组信息
     */
    private Group group;

    /**
     * 项目组下其他应用基本信息
     */
    private List<App> groupApps;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<App> getGroupApps() {
        return groupApps;
    }

    public void setGroupApps(List<App> groupApps) {
        this.groupApps = groupApps;
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "app=" + app +
                ", menu=" + menu +
                ", group=" + group +
                ", groupApps=" + groupApps +
                '}';
    }
}
