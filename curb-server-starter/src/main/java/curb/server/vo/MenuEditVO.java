package curb.server.vo;

import curb.core.model.Menu;
import curb.server.controller.SystemApiMenuController;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单编辑DTO
 *
 * @see SystemApiMenuController
 */
public class MenuEditVO implements Serializable {

    private int appId;

    private String name;

    private Integer version;

    private List<Menu> menus;

    public MenuEditVO() {
        // for deserialization
    }

    public MenuEditVO(int appId, String name, List<Menu> menus) {
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

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
