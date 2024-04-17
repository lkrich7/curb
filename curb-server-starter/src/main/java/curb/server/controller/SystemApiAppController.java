package curb.server.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.util.StringUtil;
import curb.server.converter.AppVOConverter;
import curb.server.enums.AppState;
import curb.server.po.AppPO;
import curb.server.service.AppService;
import curb.server.util.CurbServerUtil;
import curb.server.vo.AppSecretVO;
import curb.server.vo.AppVO;
import curb.server.vo.PaginationVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用管理API
 */
@RestController
@RequestMapping("/system/api/app/")
public class SystemApiAppController {

    private final AppService appService;

    public SystemApiAppController(AppService appService) {
        this.appService = appService;
    }

    /**
     * 应用列表
     *
     * @param enabled 是否只查询正常状态的应用，默认false
     * @param app     当前应用对象
     * @param group   当前项目组对象
     * @return
     */
    @GetMapping("list")
    public ApiResult<PaginationVO<AppVO>> list(@RequestParam(required = false) Boolean enabled,
                                               App app, Group group) {
        List<AppVO> rows;
        if (CurbServerUtil.isGroupApp(group, app)) {
            AppState state = enabled != null && enabled ? AppState.ENABLED : null;
            List<AppPO> list = appService.listByGroupId(group.getGroupId(), state);
            rows = AppVOConverter.convert(list);
        } else {
            AppPO po = appService.checkApp(app.getAppId(), group.getGroupId());
            rows = new ArrayList<>(1);
            rows.add(AppVOConverter.convert(po));
        }
        PaginationVO<AppVO> data = new PaginationVO<>(rows, rows.size());
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 获取指定应用信息，用于应用信息编辑
     *
     * @param appId 应用ID
     * @param group 项目组对象
     * @return
     */
    @GetMapping("get")
    public ApiResult<AppVO> getForEdit(@RequestParam int appId, Group group) {
        AppPO app = appService.checkApp(appId, group.getGroupId());
        AppVO data = AppVOConverter.convert(app);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 新建应用
     *
     * @param name  应用名称
     * @param url   应用网址
     * @param group 项目组对象
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createApp(@RequestParam String url,
                                     @RequestParam String name,
                                     Group group) {
        AppPO app = checkParam(null, url, name, group);
        appService.create(app);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 修改应用
     *
     * @param appId 应用ID
     * @param name  应用名称
     * @param url   应用网址
     * @param group 项目组对象
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updateApp(@RequestParam int appId,
                                     @RequestParam String name,
                                     @RequestParam String url,
                                     Group group) {
        AppPO app = checkParam(appId, url, name, group);
        appService.update(app);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 停用应用
     *
     * @param appId 应用ID
     * @param group 项目组对象
     * @return
     */
    @PostMapping("enable")
    public ApiResult<Void> enableApp(@RequestParam Integer appId,
                                     Group group) {
        appService.updateState(appId, group.getGroupId(), AppState.ENABLED);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 启用应用
     *
     * @param appId 应用ID
     * @param group 项目组对象
     * @return
     */
    @PostMapping("disable")
    public ApiResult<Void> disableApp(@RequestParam Integer appId,
                                      Group group) {
        appService.updateState(appId, group.getGroupId(), AppState.DISABLED);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 永久删除应用
     *
     * @param appId 应用ID
     * @param group 项目组对象
     * @return
     */
    @PostMapping("delete")
    public ApiResult<Void> deleteApp(@RequestParam Integer appId,
                                     Group group) {
        appService.delete(appId, group.getGroupId());
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取指定应用信息，用于应用信息编辑
     *
     * @param appId 应用ID
     * @param group 项目组对象
     * @return
     */
    @GetMapping("secret/get")
    public ApiResult<AppSecretVO> getAppSecret(@RequestParam int appId, Group group) {
        AppPO app = appService.checkApp(appId, group.getGroupId());
        String secret = appService.getSecret(appId);
        AppSecretVO data = new AppSecretVO();
        data.setAppId(app.getAppId());
        data.setSecret(secret);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    private AppPO checkParam(Integer appId, String url, String name, Group group) {
        name = StringUtil.trimToNull(name);
        if (name == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("应用名称不能为空");
        }
        url = StringUtil.trimToNull(url);
        if (url == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("应用网址不能为空");
        }
        // 去除末尾的斜杠
        url = url.replaceAll("/+$", "");

        AppPO app = new AppPO();
        app.setAppId(appId);
        app.setGroupId(group.getGroupId());
        app.setName(name);
        app.setUrl(url);

        return app;
    }
}
