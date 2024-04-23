package curb.server.service;

import curb.core.model.Menu;
import curb.server.dao.AppMenuDAO;
import curb.server.po.AppMenuPO;
import curb.server.util.AppMenuUtil;
import curb.server.util.CoreDataUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用菜单服务
 */
@Service
public class AppMenuService {

    private final List<Menu> headers;
    private final List<Menu> footers;
    private final AppMenuDAO appMenuDAO;

    public AppMenuService(AppMenuDAO appMenuDAO) {
        this.appMenuDAO = appMenuDAO;
        headers = CoreDataUtil.loadMenus("curb/core-data/menus-header.json");
        footers = CoreDataUtil.loadMenus("curb/core-data/menus-footer.json");
    }

    public List<Menu> list(int appId) {
        AppMenuPO po = appMenuDAO.getLatest(appId);
        if (po == null) {
            return Collections.emptyList();
        }
        return AppMenuUtil.parse(po.getMenu());
    }

    public List<Menu> listWithSystemMenu(int appId) {
        List<Menu> editables = list(appId);
        List<Menu> allMenu = new ArrayList<>(headers.size() + editables.size() + footers.size());
        allMenu.addAll(headers);
        allMenu.addAll(editables);
        allMenu.addAll(footers);
        return allMenu;
    }

    public String getEditableStr(int appId) {
        AppMenuPO po = appMenuDAO.getLatest(appId);
        return po == null ? null : po.getMenu();
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveEditable(int appId, String menuStr, int userId) {
        List<Menu> menu = AppMenuUtil.parse(menuStr);
        menuStr = AppMenuUtil.serialize(menu);
        AppMenuPO latest = appMenuDAO.getLatest(appId);
        Integer version = latest == null ? 0 : latest.getVersion() + 1;
        AppMenuPO po = new AppMenuPO();
        po.setAppId(appId);
        po.setVersion(version);
        po.setMenu(menuStr);
        po.setUserId(userId);
        appMenuDAO.insert(po);
    }

}
