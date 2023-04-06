package curb.server.service;

import curb.core.ErrorEnum;
import curb.server.dao.RoleDAO;
import curb.server.enums.RoleState;
import curb.server.enums.SystemRole;
import curb.server.po.RolePO;
import curb.server.util.CurbServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务
 */
@Service
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 检查角色状态是否正常以及是否属于项目组
     *
     * @param roleId  角色ID
     * @param groupId 项目组ID
     * @return
     */
    public RolePO checkRole(int roleId, int groupId) {
        RolePO role = roleDAO.get(roleId);
        if (role == null || role.getGroupId().intValue() != groupId) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("角色不存在");
        }
        return role;
    }

    /**
     * 查询指定项目组ID下的全部普通角色数据
     *
     * @param groupId 项目组ID
     * @return
     */
    public List<RolePO> listByGroupId(int groupId) {
        return roleDAO.listByGroupId(groupId);
    }

    /**
     * 查询指定项目组ID下的全部角色数据，包括权限系统内置角色
     *
     * @param groupId 项目组ID
     * @return
     */
    public List<RolePO> listByGroupIdWithSystem(int groupId) {
        List<RolePO> ret = roleDAO.listByGroupId(groupId);

        // 添加权限系统内置角色
        ret.add(SystemRole.GROUP_BIZ_ADMIN.toRole(groupId));
        ret.add(SystemRole.GROUP_SYS_ADMIN.toRole(groupId));
        if (CurbServerUtil.isSystemGroupId(groupId)) {
            ret.add(SystemRole.ROOT.toRole(groupId));
        }
        return ret;
    }

    /**
     * 创建角色
     *
     * @param role 角色数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(RolePO role) {
        checkSignConflict(role.getGroupId(), role.getSign(), null);
        int rows = roleDAO.insert(role);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    /**
     * 更新角色
     *
     * @param role 角色数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(RolePO role) {
        checkSystemRole(role.getRoleId());
        RolePO existed = checkRole(role.getRoleId(), role.getGroupId());
        checkSignConflict(role.getGroupId(), role.getSign(), role.getRoleId());

        existed.setSign(role.getSign());
        existed.setName(role.getName());
        existed.setDescription(role.getDescription());
        int rows = roleDAO.update(existed);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    /**
     * 更新角色状态
     *
     * @param roleId  角色ID
     * @param groupId 项目组ID
     * @param state   角色状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateState(int roleId, int groupId, RoleState state) {
        checkSystemRole(roleId);
        checkRole(roleId, groupId);

        int rows = roleDAO.updateState(roleId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    /**
     * 删除角色
     *
     * @param roleId  角色ID
     * @param groupId 项目组ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(int roleId, int groupId) {
        checkRole(roleId, groupId);
        roleDAO.delete(roleId);
        userRoleService.deleteByRoleId(roleId);
        rolePermissionService.deleteByRoleId(roleId);
    }

    private void checkSystemRole(int roleId) {
        if (SystemRole.getByRoleId(roleId) != null) {
            throw ErrorEnum.FORBIDDEN.toCurbException("系统内置角色不可修改");
        }
    }

    private void checkSignConflict(Integer groupId, String sign, Integer roleId) {
        if (SystemRole.getBySign(sign) != null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("角色标识已存在");
        }

        RolePO existed = roleDAO.getByGroupIdSign(groupId, sign);
        if (existed == null || existed.getRoleId().equals(roleId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("角色标识已存在");
    }
}
