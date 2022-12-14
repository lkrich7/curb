package curb.server.service;

import curb.core.ErrorEnum;
import curb.server.enums.RoleState;
import curb.server.enums.SystemRole;
import curb.server.dao.RoleDAO;
import curb.server.po.RolePO;
import curb.server.util.CurbServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Service
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    public RolePO checkRole(int roleId, int groupId) {
        RolePO role = roleDAO.get(roleId);
        if (role == null || role.getGroupId().intValue() != groupId) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("角色不存在");
        }
        return role;
    }

    public List<RolePO> listNormalByGroupId(int groupId) {
        return roleDAO.listByGroupIdWithoutSystem(groupId);
    }

    public List<RolePO> listAllByGroupId(int groupId) {
        List<RolePO> ret = listNormalByGroupId(groupId);

        // 添加权限系统内置角色
        ret.add(SystemRole.GROUP_BIZ_ADMIN.toRole(groupId));
        ret.add(SystemRole.GROUP_SYS_ADMIN.toRole(groupId));
        if (CurbServerUtil.isSystemGroupId(groupId)) {
            ret.add(SystemRole.ROOT.toRole(groupId));
        }
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(RolePO role) {
        checkSignConflict(role.getGroupId(), role.getSign(), null);
        int rows = roleDAO.insert(role);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(RolePO role) {
        RolePO existed = checkRole(role.getRoleId(), role.getGroupId());
        checkInternalRole(role.getSign());
        checkSignConflict(role.getGroupId(), role.getSign(), role.getRoleId());

        existed.setSign(role.getSign());
        existed.setName(role.getName());
        existed.setDescription(role.getDescription());
        int rows = roleDAO.update(existed);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateState(int roleId, int groupId, RoleState state) {
        RolePO role = checkRole(roleId, groupId);
        checkInternalRole(role.getSign());

        int rows = roleDAO.updateState(roleId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int roleId, int groupId) {
        checkRole(roleId, groupId);
        roleDAO.delete(roleId);
        userRoleService.deleteByRoleId(roleId);
        rolePermissionService.deleteByRoleId(roleId);
    }

    private void checkInternalRole(String sign) {
        if (SystemRole.getBySign(sign) != null) {
            throw ErrorEnum.FORBIDDEN.toCurbException("系统内置角色不可修改");
        }
    }

    private void checkSignConflict(Integer groupId, String sign, Integer roleId) {
        if (SystemRole.getBySign(sign) != null) {
            throw ErrorEnum.FORBIDDEN.toCurbException("角色标识已存在");
        }

        RolePO existed = roleDAO.getByGroupIdSign(groupId, sign);
        if (existed == null || existed.getRoleId().equals(roleId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("角色标识已存在");
    }
}
