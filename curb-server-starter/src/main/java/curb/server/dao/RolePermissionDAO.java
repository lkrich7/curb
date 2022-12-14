package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 角色-权限关系表数据访问层
 */
@Repository
public interface RolePermissionDAO {

    List<Integer> listPermIdByRoleId(@Param("roleId") int roleId);

    List<Integer> listRoleIdByPermId(@Param("permId") int permId);

    List<Integer> listPermIdByRoleIdsAndPermIds(@Param("roleIds") Collection<Integer> roleIds,
                                                @Param("permIds") Collection<Integer> permIds);

    int insertRolePermissions(@Param("roleId") int roleId, @Param("permIds") Collection<Integer> permIds);

    int insertPermissionRoles(@Param("permId") int permId, @Param("roleIds") Collection<Integer> roleIds);

    int deleteRolePermissions(@Param("roleId") int roleId, @Param("permIds") Collection<Integer> permIds);

    int deletePermissionRoles(@Param("permId") int permId, @Param("roleIds") Collection<Integer> roleIds);

    int deleteByPermId(@Param("permId") int permId);

    int deleteByRoleId(@Param("roleId") int roleId);
}
