package curb.server.service;

import com.google.common.collect.Sets;
import curb.core.ErrorEnum;
import curb.server.dao.PermissionDAO;
import curb.server.dao.RoleDAO;
import curb.server.dao.RolePermissionDAO;
import curb.server.po.PermissionPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(RolePermissionService.class);

    @Autowired
    private RolePermissionDAO rolePermissionDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private RoleDAO roleDAO;

    public Set<Integer> listRoleId(int permId) {
        return Sets.newTreeSet(rolePermissionDAO.listRoleIdByPermId(permId));
    }

    public Set<Integer> listPermId(int roleId, Collection<PermissionPO> permissions) {
        List<Integer> roleIds = new ArrayList<>(1);
        roleIds.add(roleId);
        return listPermId(roleIds, permissions);
    }

    public Set<Integer> listPermId(Collection<Integer> roleIds, Collection<PermissionPO> permissions) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptySet();
        }
        List<Integer> permIds = toPermIds(permissions);
        permIds = rolePermissionDAO.listPermIdByRoleIdsAndPermIds(roleIds, permIds);
        Set<Integer> ret = new TreeSet<>();
        ret.addAll(permIds);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePermissionRoles(int permId, int groupId, Collection<Integer> newRoleIds) {
        Set<Integer> newSet = new TreeSet<>();
        newSet.addAll(roleDAO.listRoleIdByGroupId(groupId));
        newSet.retainAll(newRoleIds);

        Set<Integer> oldSet = listRoleId(permId);

        Set<Integer> delete = Sets.difference(oldSet, newSet);
        Set<Integer> insert = Sets.difference(newSet, oldSet);

        if (!delete.isEmpty()) {
            int deleteRows = rolePermissionDAO.deletePermissionRoles(permId, delete);
            if (deleteRows != delete.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        if (!insert.isEmpty()) {
            int insertRows = rolePermissionDAO.insertPermissionRoles(permId, insert);
            if (insertRows != insert.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        LOG.info("save permission(permId={},groupId={}) roles: delete:{}, insert:{}", permId, groupId, delete, insert);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRolePermissions(int roleId, int appId, Collection<Integer> newPermIds) {
        List<PermissionPO> perms = permissionDAO.listByAppId(appId);
        List<Integer> appAllPermIds = toPermIds(perms);
        Set<Integer> newSet = new TreeSet<>();
        newSet.addAll(newPermIds);
        newSet.retainAll(appAllPermIds);

        Set<Integer> oldSet = listPermId(roleId, perms);

        Set<Integer> delete = Sets.difference(oldSet, newSet);
        Set<Integer> insert = Sets.difference(newSet, oldSet);

        if (!delete.isEmpty()) {
            int deleteRows = rolePermissionDAO.deleteRolePermissions(roleId, delete);
            if (deleteRows != delete.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        if (!insert.isEmpty()) {
            int insertRows = rolePermissionDAO.insertRolePermissions(roleId, insert);
            if (insertRows != insert.size()) {
                throw ErrorEnum.SERVER_ERROR.toCurbException();
            }
        }
        LOG.info("save role(roleId={},appId={}) permissions: delete:{}, insert:{}", roleId, appId, delete, insert);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByPermId(int permId) {
        rolePermissionDAO.deleteByPermId(permId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(int roleId) {
        rolePermissionDAO.deleteByRoleId(roleId);
    }

    private List<Integer> toPermIds(Collection<PermissionPO> permissions) {
        return permissions.stream().map(PermissionPO::getPermId).collect(Collectors.toList());
    }

}
