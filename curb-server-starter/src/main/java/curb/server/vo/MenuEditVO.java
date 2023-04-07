package curb.server.vo;

import java.io.Serializable;

/**
 * 菜单编辑View Object
 *
 * @see curb.server.controller.SystemApiMenuController
 */
public class MenuEditVO implements Serializable {

    private int appId;

    private String name;

    private String menus;

    public MenuEditVO() {
        // for deserialization
    }

    public MenuEditVO(int appId, String name, String menus) {
        this.appId = appId;
        this.name = name;
        this.menus = menus;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenus() {
        return menus;
    }

    public void setMenus(String menus) {
        this.menus = menus;
    }
}
