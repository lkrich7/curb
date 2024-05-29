package curb.server.service;

import curb.core.CurbDataProvider;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.util.CurbUtil;
import curb.core.util.ServletUtil;
import curb.server.bo.CurbToken;
import curb.server.configuration.CurbServerProperties;
import curb.server.converter.GroupConverter;
import curb.server.po.AppPO;
import curb.server.po.GroupPO;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * 服务端数据服务
 */
public class CurbServerDataProvider implements CurbDataProvider {

    private final GroupService groupService;

    private final AppService appService;

    private final UserService userService;

    private final UserPermissionService userPermissionService;

    private final CurbServerProperties properties;

    public CurbServerDataProvider(GroupService groupService,
                                  AppService appService,
                                  UserService userService,
                                  UserPermissionService userPermissionService,
                                  CurbServerProperties properties) {
        this.groupService = groupService;
        this.appService = appService;
        this.userService = userService;
        this.userPermissionService = userPermissionService;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "CurbServerDataProvider{" +
                "groupService=" + groupService +
                ", appService=" + appService +
                ", userService=" + userService +
                ", userPermissionService=" + userPermissionService +
                '}';
    }

    @Override
    public App getApp(HttpServletRequest request) {
        AppPO appPO = getAppPO(request);
        return toApp(appPO);
    }

    private AppPO getAppPO(HttpServletRequest request) {
        String url = CurbUtil.getUrl(request);
        AppPO appPO = appService.findLongestMatch(url);
        if (appPO == null) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return appPO;
    }

    private App toApp(AppPO appPO) {
        App ret = new App();
        ret.setAppId(appPO.getAppId());
        ret.setGroupId(appPO.getGroupId());
        ret.setUrl(URI.create(appPO.getUrl()));
        ret.setName(appPO.getName());
        return ret;
    }

    @Override
    public Group getGroup(HttpServletRequest request) {
        App app = CurbUtil.getApp(request);
        if (app == null) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        GroupPO groupPO = groupService.getById(app.getGroupId());
        if (groupPO == null) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return GroupConverter.convert(groupPO);
    }

    @Override
    public User getUser(HttpServletRequest request) {
        String encryptedToken = ServletUtil.getCookie(request, properties.getTokenName());
        Group group = CurbUtil.getGroup(request);
        String groupSecret = groupService.getGroupSecret(group.getGroupId());
        CurbServerProperties.TokenProperties tokenProperties = properties.getToken();
        CurbToken token = CurbToken.decrypt(encryptedToken, groupSecret, tokenProperties.getAlgorithm());
        if (token == null || token.isExpired(tokenProperties.getTtl())) {
            return null;
        }
        return userService.getByUsername(token.getKey());
    }

    @Override
    public UserAppPermissions getUserAppPermissions(User user, App app, Group group) {
        if (user == null) {
            return UserAppPermissions.none();
        }
        return userPermissionService.getUserAppPermissions(group, app, user.getUserId());
    }

}
