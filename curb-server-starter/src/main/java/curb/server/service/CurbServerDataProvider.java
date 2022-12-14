package curb.server.service;

import curb.core.CurbDataProvider;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.util.CurbUtil;
import curb.server.bo.CurbToken;
import curb.server.util.GroupUtil;
import curb.core.ErrorEnum;
import curb.server.po.AppPO;
import curb.server.po.GroupPO;
import curb.server.util.CurbServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务端数据服务
 */
@Service
public class CurbServerDataProvider implements CurbDataProvider {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Override
    public App getApp(HttpServletRequest request) {
        AppPO appPO = getAppPO(request);
        return toApp(appPO);
    }

    private App toApp(AppPO appPO) {
        App ret = new App();
        ret.setAppId(appPO.getAppId());
        ret.setDomain(appPO.getDomain());
        ret.setMainPage(appPO.getMainPage());
        ret.setName(appPO.getName());
        ret.setDescription(appPO.getDescription());
        return ret;
    }

    private AppPO getAppPO(HttpServletRequest request) {
        String domain = CurbServerUtil.getDomain(request);
        AppPO appPO = appService.getByDomain(domain);

        if (appPO == null) {
            GroupPO groupDto = groupService.getByDomain(domain);
            if (groupDto == null) {
                throw ErrorEnum.NOT_FOUND.toCurbException();
            }
            appPO = appService.get(CurbServerUtil.CURB_APP_ID);
            appPO.setGroupId(groupDto.getGroupId());
            appPO.setDomain(groupDto.getDomain());
            appPO.setMainPage("/");
            appPO.setName(groupDto.getName());
            appPO.setDescription(groupDto.getName());
        }

        return appPO;
    }

    @Override
    public Group getGroup(HttpServletRequest request) {
        String domain = CurbServerUtil.getDomain(request);
        GroupPO groupPO = groupService.getByDomain(domain);
        if (groupPO == null) {
            AppPO app = appService.getByDomain(domain);
            if (app != null) {
                groupPO = groupService.getById(app.getGroupId());
            }
        }
        if (groupPO == null) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }

        return GroupUtil.fromPO(groupPO);
    }

    @Override
    public User getUser(HttpServletRequest request) {
        String encrypted = CurbUtil.getToken(request);
        Group group = CurbUtil.getGroup(request);
        String groupSecret = groupService.getGroupSecret(group.getGroupId());
        CurbToken token = CurbToken.decrypt(encrypted, groupSecret);
        if (token == null || CurbUtil.isTokenExpired(token.getTs())) {
            return null;
        }
        return userService.getByEmail(token.getEmail());
    }

    @Override
    public UserAppPermissions getUserAppPermissions(User user, App app, Group group) {
        if (user == null) {
            return UserAppPermissions.none();
        }
        return userPermissionService.getUserAppPermissions(group, app, user.getUserId());
    }

}
