package curb.server.service;

import com.google.common.collect.Sets;
import curb.core.ErrorEnum;
import curb.core.model.User;
import curb.server.dao.RoleDAO;
import curb.server.dao.UserDAO;
import curb.server.dao.UserRoleDAO;
import curb.server.dao.UserRoleSystemDAO;
import curb.server.enums.SystemRole;
import curb.server.po.RolePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleDAO userRoleDAO;

    private final UserRoleSystemDAO userRoleSystemDAO;

    private final RoleDAO roleDAO;

    private final UserDAO userDAO;

    public UserRoleService(UserRoleDAO userRoleDAO,
                           UserRoleSystemDAO userRoleSystemDAO,
                           RoleDAO roleDAO,
                           UserDAO userDAO) {
        this.userRoleDAO = userRoleDAO;
        this.userRoleSystemDAO = userRoleSystemDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
    }

    public List<User> listUserByRoleId(int roleId, int groupId) {
        List<Integer> userIds;
        if (SystemRole.isSystemRoleId(roleId)) {
            userIds = userRoleSystemDAO.listUserId(groupId, roleId);
        } else {
            RolePO role = roleDAO.get(roleId);
            if (role == null || role.getGroupId().intValue() != groupId) {
                return Collections.emptyList();
            }
            userIds = userRoleDAO.listUserIdByRoleId(roleId);
        }
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userDAO.listByUserIds(userIds);
    }

    /**
     * 查询用户 在指定项目组拥有的角色ID
     *
     * @param userId     用户ID
     * @param groupId    项目组ID
     * @param groupRoles 项目组的全部角色
     * @return
     */
    public Set<Integer> listRoleIdByUserId(int userId, int groupId, List<RolePO> groupRoles) {
        List<Integer> normalRoleIds;
        if (groupRoles == null || groupRoles.isEmpty()) {
            normalRoleIds = Collections.emptyList();
        } else {
            Set<Integer> groupRoleIds = groupRoles.stream().map(RolePO::getRoleId).collect(Collectors.toSet());
            if (!groupRoleIds.isEmpty()) {
                normalRoleIds = userRoleDAO.listRoleIdByUserId(userId, groupRoleIds);
            } else {
                normalRoleIds = Collections.emptyList();
            }
        }
        List<Integer> systemRoleIds = userRoleSystemDAO.listRoleId(groupId, userId);
        Set<Integer> ret = new TreeSet<>();
        ret.addAll(normalRoleIds);
        ret.removeAll(SystemRole.ALL_ROLE_IDS);
        ret.addAll(systemRoleIds);
        return ret;
    }

    public List<Integer> listEnabledRoleIdByUserId(int userId) {
        return userRoleDAO.listEnabledRoleIdByUserId(userId);
    }

    public List<Integer> listSystemRoleId(int userId, int groupId) {
        return userRoleSystemDAO.listRoleId(groupId, userId);
    }

    /**
     * 保存角色的用户配置
     *
     * @param roleId     角色ID
     * @param groupId    项目组ID
     * @param userIds    用户ID
     * @param operUserId 当前操作用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleUsers(int roleId, int groupId, Collection<Integer> userIds, int operUserId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        TreeSet<Integer> news = Sets.newTreeSet(userIds);
        List<User> users = userDAO.listByUserIds(news);
        if (users.size() != news.size()) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户不存在");
        }
        SystemRole systemRole = SystemRole.getByRoleId(roleId);
        if (systemRole == null) {
            // 普通业务角色
            saveNormalRoleUsers(roleId, groupId, news);
        } else {
            // 系统角色
            saveSystemRoleUsers(systemRole, groupId, news, operUserId);
        }
    }

    /**
     * 保存用户的角色配置
     *
     * @param userId     用户ID
     * @param groupId    项目组ID
     * @param normal     配置的普通角色ID
     * @param system     配置的系统角色ID
     * @param operUserId 当前操作用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUserRoles(int userId, int groupId, Set<Integer> normal, Set<SystemRole> system, int operUserId) {
        saveUserNormalRoles(userId, groupId, normal);
        saveUserSystemRoles(userId, groupId, system, operUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(int userId) {
        userRoleDAO.deleteByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(int roleId) {
        userRoleDAO.deleteByRoleId(roleId);
    }

    private void saveUserNormalRoles(int userId, int groupId, Set<Integer> news) {
        List<Integer> groupNormalRoleIds = roleDAO.listRoleIdByGroupId(groupId);
        Set<Integer> roleIdSet = new TreeSet<>();
        roleIdSet.addAll(news);
        roleIdSet.retainAll(groupNormalRoleIds);
        if (news.size() != roleIdSet.size()) {
            news.removeAll(roleIdSet);
            throw ErrorEnum.PARAM_ERROR.toCurbException("角色ID错误:" + news);
        }

        Set<Integer> olds = new TreeSet<>();
        if (!groupNormalRoleIds.isEmpty()) {
            olds.addAll(userRoleDAO.listRoleIdByUserId(userId, groupNormalRoleIds));
        }

        Set<Integer> delete = Sets.difference(olds, news);
        Set<Integer> insert = Sets.difference(news, olds);

        if (!delete.isEmpty()) {
            int deleteRows = userRoleDAO.deleteForUserId(userId, delete);
            if (deleteRows != delete.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        if (!insert.isEmpty()) {
            int insertRows = userRoleDAO.insertForUserId(userId, insert);
            if (insertRows != insert.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        LOG.info("save normal role for userId:{}, groupId:{}, delete:{}, insert:{}", userId, groupId, delete, insert);
    }

    private void saveUserSystemRoles(int userId, int groupId, Set<SystemRole> news, int operUserId) {
        EnumSet<SystemRole> olds = getUserSystemRoles(groupId, userId);

        Set<SystemRole> delete = Sets.difference(olds, news);
        Set<SystemRole> insert = Sets.difference(news, olds);

        if (delete.isEmpty() && insert.isEmpty()) {
            //没有系统权限需要修改 直接返回
            LOG.info("save system role for userId:{}, groupId:{}, delete:{}, insert:{}", userId, groupId, delete, insert);
            return;
        }

        // 检查当前操作用户是否能给用户修改系统权限
        EnumSet<SystemRole> operators = getUserSystemRoles(groupId, operUserId);
        if (!delete.isEmpty()) {
            for (SystemRole role : delete) {
                if (!SystemRole.canRevoke(operators, role, userId == operUserId)) {
                    throw ErrorEnum.PARAM_ERROR.toCurbException("你无权删除用户角色: " + role.getName());
                }
            }
            List<Integer> roleIds = delete.stream().map(SystemRole::getRoleId).collect(Collectors.toList());
            userRoleSystemDAO.deleteForUser(groupId, userId, roleIds);
        }
        if (!insert.isEmpty()) {
            for (SystemRole role : insert) {
                if (!SystemRole.canGrant(operators, role)) {
                    throw ErrorEnum.PARAM_ERROR.toCurbException("你无权添加用户角色：" + role.getName());
                }
            }
            List<Integer> roleIds = insert.stream().map(SystemRole::getRoleId).collect(Collectors.toList());
            userRoleSystemDAO.insertForUser(groupId, userId, roleIds);
        }

        LOG.info("save system role for userId:{}, groupId:{}, delete:{}, insert:{}", userId, groupId, delete, insert);
    }

    private void saveNormalRoleUsers(int roleId, int groupId, TreeSet<Integer> news) {
        Set<Integer> olds = Sets.newTreeSet(userRoleDAO.listUserIdByRoleId(roleId));
        Set<Integer> delete = Sets.difference(olds, news);
        Set<Integer> insert = Sets.difference(news, olds);
        if (!delete.isEmpty()) {
            userRoleDAO.deleteForRoleId(roleId, delete);
        }
        if (!insert.isEmpty()) {
            userRoleDAO.insertForRoleId(roleId, insert);
        }
        LOG.info("save normal role id={}, groupId:{}, delete userIds:{}, insert userIds:{}",
                roleId, groupId, delete, insert);
    }

    private void saveSystemRoleUsers(SystemRole systemRole, int groupId, TreeSet<Integer> news, int operUserId) {
        int roleId = systemRole.getRoleId();
        EnumSet<SystemRole> operSystemRoles = getUserSystemRoles(groupId, operUserId);
        Set<Integer> olds = Sets.newTreeSet(userRoleSystemDAO.listUserId(groupId, roleId));
        Set<Integer> delete = Sets.difference(olds, news);
        Set<Integer> insert = Sets.difference(news, olds);
        if (!delete.isEmpty()) {
            boolean isSelf = delete.size() == 1 && delete.contains(operUserId);
            if (!SystemRole.canRevoke(operSystemRoles, systemRole, isSelf)) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("你无权删除用户角色: " + systemRole.getName());
            }
            userRoleSystemDAO.deleteForRole(groupId, roleId, delete);
        }
        if (!insert.isEmpty()) {
            if (!SystemRole.canGrant(operSystemRoles, systemRole)) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("你无权添加用户角色：" + systemRole.getName());
            }
            userRoleSystemDAO.insertForRole(groupId, roleId, insert);
        }
        LOG.info("save system role {}, groupId:{}, delete userIds:{}, insert userIds:{}",
                systemRole, groupId, delete, insert);
    }

    private EnumSet<SystemRole> getUserSystemRoles(int groupId, int userId) {
        List<Integer> list = userRoleSystemDAO.listRoleId(groupId, userId);
        EnumSet<SystemRole> operSystem = EnumSet.noneOf(SystemRole.class);
        for (Integer i : list) {
            operSystem.add(SystemRole.getByRoleId(i));
        }
        return operSystem;
    }

}
