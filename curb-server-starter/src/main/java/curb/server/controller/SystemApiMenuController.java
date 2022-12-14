package curb.server.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.server.po.AppPO;
import curb.server.service.AppMenuService;
import curb.server.service.AppService;
import curb.server.util.AppMenuUtil;
import curb.server.vo.MenuEditVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单管理API
 */
@RestController
@RequestMapping("/system/api/menu/")
public class SystemApiMenuController {

    @Autowired
    private AppService appService;

    @Autowired
    private AppMenuService appMenuService;

    @GetMapping(value = "edit/get")
    public ApiResult<MenuEditVO> getForEdit(@RequestParam(required = false) Integer appId,
                                            App app, Group group) {
        AppPO appPO = appService.checkApp(appId, app, group);
        appId = appPO.getAppId();
        String menus = appMenuService.getEditableStr(appId);

        return ErrorEnum.SUCCESS.toApiResult(new MenuEditVO(appId, appPO.getName(), AppMenuUtil.parse(menus)));
    }

    @PostMapping(value = "edit/save")
    public ApiResult<Void> saveEdit(@RequestParam(required = false) Integer appId,
                                    @RequestParam String menus,
                                    App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        appMenuService.saveEditable(appId, menus);

        return ErrorEnum.SUCCESS.toApiResult();
    }
}
