package curb.server.service;


import curb.core.util.JsonUtil;
import curb.server.dao.MenuDAO;
import curb.core.model.Menu;
import curb.server.util.AppMenuUtil;
import curb.server.util.CoreDataUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
@Service
public class AppMenuService {

    private final List<Menu> headers;
    private final List<Menu> footers;
    @Autowired
    private MenuDAO menuDAO;

    public AppMenuService() {
        headers = CoreDataUtil.loadMenus("curb/core-data/menus-header.json");
        footers = CoreDataUtil.loadMenus("curb/core-data/menus-footer.json");
    }

    public List<Menu> list(int appId) {
        String menu = menuDAO.get(appId);
        return AppMenuUtil.parse(menu);
    }

    public List<Menu> listWithSystemMenu(int appId) {
        List<Menu> editables = list(appId);
        List<Menu> allMenu = new ArrayList<>(headers.size() + editables.size() + footers.size());
        allMenu.addAll(headers);
        allMenu.addAll(editables);
        allMenu.addAll(footers);
        return allMenu;
    }

    public String getEditableStr(int appid) {
        return menuDAO.get(appid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveEditable(int appId, String menuStr) {
        List<Menu> menu = AppMenuUtil.parse(menuStr);
        menuStr = AppMenuUtil.serialize(menu);
        int row = menuDAO.update(appId, menuStr);
        if (row < 1) {
            menuDAO.insert(appId, menuStr);
        }
    }

}
