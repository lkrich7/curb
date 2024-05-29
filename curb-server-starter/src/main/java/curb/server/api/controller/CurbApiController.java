package curb.server.api.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.AppDetail;
import curb.core.model.Group;
import curb.core.model.Menu;
import curb.core.model.User;
import curb.core.model.UserPermission;
import curb.core.model.UserState;
import curb.server.bo.CurbToken;
import curb.server.configuration.CurbServerProperties;
import curb.server.enums.AppState;
import curb.server.po.AppPO;
import curb.server.po.GroupPO;
import curb.server.service.AppMenuService;
import curb.server.service.AppService;
import curb.server.service.GroupService;
import curb.server.service.UserPermissionService;
import curb.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Curb API 接口
 */
@RestController
public class CurbApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbApiController.class);

    private final UserService userService;

    private final AppService appService;

    private final GroupService groupService;

    private final UserPermissionService userPermissionService;

    private final AppMenuService appMenuService;

    private final CurbServerProperties properties;

    public CurbApiController(UserService userService,
                             AppService appService, GroupService groupService,
                             UserPermissionService userPermissionService,
                             AppMenuService appMenuService,
                             CurbServerProperties properties) {
        this.userService = userService;
        this.appService = appService;
        this.groupService = groupService;
        this.userPermissionService = userPermissionService;
        this.appMenuService = appMenuService;
        this.properties = properties;
    }

    /**
     * 查询应用详细信息
     *
     * @param appId 应用ID
     * @return
     */
    @RequestMapping("/api/app/detail")
    public ApiResult<AppDetail> getAppDetail(@RequestParam(value = "appId") Integer appId) {
        AppPO appPO = appService.get(appId);
        GroupPO groupPO = groupService.getById(appPO.getGroupId());
        App app = toApp(appPO);
        List<Menu> menu = appMenuService.list(appId);
        Group group = toGroup(groupPO);
        List<App> apps = listGroupApps(appPO.getGroupId());

        AppDetail appDetail = new AppDetail();
        appDetail.setApp(app);
        appDetail.setMenu(menu);
        appDetail.setGroup(group);
        appDetail.setGroupApps(apps);

        return ErrorEnum.SUCCESS.toApiResult(appDetail);
    }

    /**
     * 根据Curb的用户登录态串查询用户信息
     *
     * @param appId 应用ID
     * @param token 用户登录态串
     * @return
     */
    @RequestMapping("/api/user/info")
    public ApiResult<User> getUserInfo(@RequestParam(value = "appId") Integer appId, @RequestParam(value = "token") String token) {
        AppPO app = appService.get(appId);
        if (app == null) {
            return ErrorEnum.PARAM_ERROR.toApiResult(null, "非法参数appId");
        }
        String groupSecret = groupService.getGroupSecret(app.getGroupId());

        CurbServerProperties.TokenProperties tokenProperties = properties.getToken();;

        CurbToken curbToken = CurbToken.decrypt(token, groupSecret, tokenProperties.getAlgorithm());
        if (curbToken == null) {
            return ErrorEnum.NEED_LOGIN.toApiResult();
        }
        if (curbToken.isExpired(tokenProperties.getTtl())) {
            return ErrorEnum.NEED_LOGIN.toApiResult();
        }
        String username = curbToken.getKey();
        User user = userService.getByUsername(username);

        UserState state = UserState.check(user);
        if (state.isOk()) {
            return ErrorEnum.SUCCESS.toApiResult(user);
        } else if (state == UserState.BLOCKED) {
            return ErrorEnum.USER_BLOCKED.toApiResult();
        } else {
            return ErrorEnum.NEED_LOGIN.toApiResult();
        }
    }

    /**
     * 查询应用权限并给出是否用户拥有该权限
     *
     * @param appId
     * @param userId
     * @return
     */
    @RequestMapping("/api/permission/user/list")
    public ApiResult<List<UserPermission>> listUserPermission(@RequestParam(value = "appId") Integer appId, @RequestParam(value = "userId") Integer userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ErrorEnum.PARAM_ERROR.toApiResult(null, "非法的用户信息");
        }
        List<UserPermission> permList = userPermissionService.listUserPermission(appId, user.getUserId());
        LOGGER.info("appId:{} user:{}, username:{} get Permission List", appId, userId, user.getUsername());
        return ErrorEnum.SUCCESS.toApiResult(permList);
    }

    private App toApp(AppPO po) {
        if (po == null) {
            return null;
        }
        App tmp = new App();
        tmp.setAppId(po.getAppId());
        tmp.setName(po.getName());
        tmp.setUrl(URI.create(po.getUrl()));
        return tmp;
    }

    private Group toGroup(GroupPO group) {
        Group ret = new Group();
        ret.setName(group.getName());
        ret.setUrl(URI.create(group.getUrl()));
        ret.setGroupId(group.getGroupId());
        return ret;
    }

    private List<App> listGroupApps(Integer groupId) {
        List<AppPO> appPOList = appService.listByGroupId(groupId, AppState.ENABLED);
        List<App> apps = new ArrayList<>(appPOList.size());
        for (AppPO po : appPOList) {
            App app = toApp(po);
            apps.add(app);
        }
        return apps;
    }
}
