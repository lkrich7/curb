package curb.server.service;

import curb.core.ErrorEnum;
import curb.server.dao.PermissionDAO;
import curb.server.enums.PermissionState;
import curb.server.po.PermissionPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 *
 */
@Service
public class PermissionService {

    private final PermissionDAO permissionDAO;

    private final RolePermissionService rolePermissionService;

    public PermissionService(PermissionDAO permissionDAO, RolePermissionService rolePermissionService) {
        this.permissionDAO = permissionDAO;
        this.rolePermissionService = rolePermissionService;
    }

    public PermissionPO checkPermission(int permId, int appId) {
        PermissionPO permission = permissionDAO.get(permId);
        if (permission == null || permission.getAppId() != appId) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return permission;
    }

    public List<PermissionPO> listByAppId(int appId) {
        return permissionDAO.listByAppId(appId);
    }

    public List<PermissionPO> listEnabledByAppId(int appId) {
        return permissionDAO.listByAppIdState(appId, PermissionState.ENABLED.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(PermissionPO permission) {
        checkSignConflict(permission.getSign(), permission.getAppId());
        permission.setState(PermissionState.ENABLED.getCode());
        int rows = permissionDAO.insert(permission);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionPO permission) {
        PermissionPO existed = checkPermission(permission.getPermId(), permission.getAppId());
        existed.setSign(permission.getSign());
        existed.setName(permission.getName());
        existed.setDescription(permission.getDescription());
        int rows = permissionDAO.update(existed);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateState(int permId, int appId, PermissionState state) {
        checkPermission(permId, appId);
        int rows = permissionDAO.updateState(permId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int permId, int appId) {
        checkPermission(permId, appId);
        int rows = permissionDAO.delete(permId);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        rolePermissionService.deleteByPermId(permId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletByAppId(int appId) {
        List<PermissionPO> list = permissionDAO.listByAppId(appId);
        for (PermissionPO po : list) {
            rolePermissionService.deleteByPermId(po.getPermId());
            permissionDAO.delete(po.getPermId());
        }
    }

    private void checkSignConflict(String sign, Integer appId) {
        PermissionPO existed = permissionDAO.getByAppIdSign(appId, sign);
        if (existed == null || existed.getAppId().equals(appId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("权限标识已存在");
    }

}
