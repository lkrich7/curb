package curb.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import curb.server.po.RolePO;

/**
 * 角色信息表数据访问层
 */
@Repository
public interface RoleDAO {

    RolePO get(@Param("roleId") int roleId);

    RolePO getByGroupIdSign(@Param("groupId") int groupId, @Param("sign") String sign);

    List<RolePO> listByRoleIds(@Param("roleIds") List<Integer> roleIds);

    int insert(RolePO role);

    int update(RolePO role);

    int updateState(@Param("roleId") int roleId, @Param("state") int state);

    int delete(@Param("roleId") int roleId);

    List<RolePO> listByGroupIdWithoutSystem(int groupId);

    List<RolePO> listByGroupId(int groupId);

    List<Integer> listRoleIdByGroupId(int groupId);

}
