package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 用户-角色关系表数据访问层
 */
@Repository
public interface UserRoleDAO {

    List<Integer> listEnabledRoleIdByUserId(int userId);

    List<Integer> listRoleIdByUserId(@Param("userId") int userId, @Param("roleIds") Collection<Integer> roleIds);

    List<Integer> listUserIdByRoleId(@Param("roleId") int roleId);

    int insertForUserId(@Param("userId") int userId, @Param("roleIds") Collection<Integer> roleIds);

    int insertForRoleId(@Param("roleId") int roleId, @Param("userIds") Collection<Integer> userIds);

    int delete(@Param("userId") int userId, @Param("roleId") int roleId);

    int deleteForUserId(@Param("userId") int userId, @Param("roleIds") Collection<Integer> roleIds);

    int deleteForRoleId(@Param("roleId") int roleId, @Param("userIds") Collection<Integer> userIds);

    /**
     * 删除指定用户的全部角色配置
     *
     * @param userId yonghuID
     * @return
     */
    int deleteByUserId(@Param("userId") int userId);

    /**
     * 删除指定角色的全部用户配置
     * @param roleId
     * @return
     */
    int deleteByRoleId(@Param("roleId") int roleId);

}
