package curb.core.util;

import curb.core.model.Menu;
import curb.core.model.Permission;
import curb.core.model.UserAppPermissions;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单结构构造工具类
 */
public final class MenuUtil {

    private MenuUtil() {
    }

    /**
     * 根据用户的应用权限过滤应用菜单
     *
     * @param appMenu
     * @param userPerms
     * @return
     */
    public static List<Menu> buildUserMenu(List<Menu> appMenu, UserAppPermissions userPerms) {
        if (appMenu == null || appMenu.isEmpty()) {
            return new ArrayList<>();
        }

        List<Menu> ret = new ArrayList<>(appMenu.size());
        for (Menu menu : appMenu) {
            menu = copyMenu(menu);
            List<Menu> children = menu.getChildren();
            if (children != null && !children.isEmpty()) {
                children = buildUserMenu(children, userPerms);
                menu.setChildren(children);
            }
            if (children != null && !children.isEmpty()) {
                ret.add(menu);
            } else if (menu.isSkipCheck()) {
                ret.add(menu);
            } else {
                String url = menu.getUrl();
                Permission perm = Permission.build(url);
                if (userPerms.isAllowed(perm)) {
                    ret.add(menu);
                }
            }
        }
        return ret;
    }

    private static Menu copyMenu(Menu menu) {
        Menu copy = new Menu();
        BeanUtils.copyProperties(menu, copy);
        return copy;
    }
}
