package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 系统内置角色用户配置表
 */
@Repository
public interface UserRoleSystemDAO {

    List<Integer> listRoleId(@Param("groupId") int groupId, @Param("userId") int userId);

    List<Integer> listUserId(@Param("groupId") int groupId, @Param("roleId") int roleId);

    int insertForUser(@Param("groupId") int groupId, @Param("userId") int userId, @Param("roleIds") Collection<Integer> roleIds);

    int insertForRole(@Param("groupId") int groupId, @Param("roleId") int roleId, @Param("userIds") Collection<Integer> userIds);

    int deleteForUser(@Param("groupId") int groupId, @Param("userId") int userId, @Param("roleIds") Collection<Integer> roleIds);

    int deleteForRole(@Param("groupId") int groupId, @Param("roleId") int roleId, @Param("userIds") Collection<Integer> userIds);
}
