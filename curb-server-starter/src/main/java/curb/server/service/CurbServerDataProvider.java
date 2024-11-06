package curb.server.service;

import curb.core.CurbDataProvider;
import curb.core.CurbRequestContext;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.server.bo.CurbToken;
import curb.server.configuration.CurbServerProperties;
import curb.server.converter.GroupConverter;
import curb.server.po.AppPO;
import curb.server.po.GroupPO;
import curb.server.po.OpLogPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

/**
 * 服务端数据服务
 */
public class CurbServerDataProvider implements CurbDataProvider {

    private final GroupService groupService;

    private final AppService appService;

    private final UserService userService;

    private final UserPermissionService userPermissionService;

    private final OpLogService opLogService;

    private final CurbServerProperties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbServerDataProvider.class);

    public CurbServerDataProvider(GroupService groupService,
                                  AppService appService,
                                  UserService userService,
                                  UserPermissionService userPermissionService,
                                  OpLogService opLogService,
                                  CurbServerProperties properties) {
        this.groupService = groupService;
        this.appService = appService;
        this.userService = userService;
        this.userPermissionService = userPermissionService;
        this.opLogService = opLogService;
        this.properties = properties;
    }

    @Override
    public App getApp(String url) {
        AppPO appPO = getAppPO(url);
        return toApp(appPO);
    }

    private AppPO getAppPO(String url) {
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
    public Group getGroup(CurbRequestContext request) {
        App app = request.getApp();
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
    public User getUser(String encryptedToken, CurbRequestContext context) {
        Group group = context.getGroup();
        String groupSecret = groupService.getGroupSecret(group.getGroupId());
        CurbServerProperties.TokenProperties tokenProperties = properties.getToken();
        CurbToken token = CurbToken.decrypt(encryptedToken, groupSecret, tokenProperties.getAlgorithm());
        if (token == null || token.isExpired(tokenProperties.getTtl())) {
            return null;
        }
        return userService.getByUsername(token.getKey());
    }

    @Override
    public UserAppPermissions getUserAppPermissions(CurbRequestContext context) {
        User user = context.getUser();
        if (user == null) {
            return UserAppPermissions.none();
        }
        Group group = context.getGroup();
        App app = context.getApp();
        return userPermissionService.getUserAppPermissions(group, app, user.getUserId());
    }

    @Override
    public void recordRequest(CurbRequestContext context) {
        try {
            OpLogPO po = new OpLogPO();
            po.setEventTime(new Date(context.getStartTime()));
            po.setUsername(Optional.ofNullable(context.getUser()).map(User::getUsername).orElse(""));
            po.setIp(context.getIp());
            po.setGroupId(context.getGroup().getGroupId());
            po.setAppId(context.getApp().getAppId());
            po.setMethod(context.getMethod());
            po.setUrl(context.getUrl());
            po.setCost(context.costMs());
            po.setState(context.getAccessState().getCode());
            po.setResult("");
            opLogService.create(po);
        } catch (Exception e) {
            LOGGER.error("error on record request.(context:{}): {}", context, e.getMessage(), e);
        }
    }

}
