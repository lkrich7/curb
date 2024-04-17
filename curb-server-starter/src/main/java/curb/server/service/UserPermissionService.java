package curb.server.service;

import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.Permission;
import curb.core.model.UserAppPermissions;
import curb.core.model.UserPermission;
import curb.server.dao.PermissionDAO;
import curb.server.enums.PermissionState;
import curb.server.enums.SystemRole;
import curb.server.po.PermissionPO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserPermissionService {

    private final PermissionDAO permissionDAO;

    private final UserRoleService userRoleService;

    private final RolePermissionService rolePermissionService;

    public UserPermissionService(PermissionDAO permissionDAO,
                                 UserRoleService userRoleService,
                                 RolePermissionService rolePermissionService) {
        this.permissionDAO = permissionDAO;
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
    }

    public List<UserPermission> listUserPermission(Integer appId, Integer userId) {
        List<PermissionPO> appPermList = permissionDAO.listByAppIdState(appId, PermissionState.ENABLED.getCode());
        if (appPermList == null) {
            return Collections.emptyList();
        }

        List<UserPermission> ret = new ArrayList<>(appPermList.size());

        List<Integer> userRoleIdList = userRoleService.listEnabledRoleIdByUserId(userId);
        if (userRoleIdList == null || userRoleIdList.isEmpty()) {
            return ret;
        }

        Set<Integer> appRolePermIds = rolePermissionService.listPermId(userRoleIdList, appPermList);

        for (PermissionPO perm : appPermList) {
            boolean userChecked = appRolePermIds != null && appRolePermIds.contains(perm.getPermId());
            UserPermission userPerm = new UserPermission();
            userPerm.setSign(perm.getSign());
            userPerm.setUserChecked(userChecked);
            ret.add(userPerm);
        }
        return ret;
    }

    public UserAppPermissions getUserPermissions(Integer appId, Integer userId) {
        List<UserPermission> userPermissions = listUserPermission(appId, userId);
        return UserAppPermissions.build(userPermissions);
    }

    public UserAppPermissions getUserAppPermissions(Group group, App app, Integer userId) {
        List<UserPermission> userAppPermissionList = listUserPermission(app.getAppId(), userId);

        List<Integer> userSystemRoleId = userRoleService.listSystemRoleId(userId, group.getGroupId());
        List<Permission> userSystemPermissionList = SystemRole.toPermissionList(userSystemRoleId, group.getGroupId());
        return UserAppPermissions.build(userSystemPermissionList, userAppPermissionList);
    }


}
