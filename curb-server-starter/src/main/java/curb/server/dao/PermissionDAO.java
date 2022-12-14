package curb.server.dao;

import curb.server.po.PermissionPO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限信息表数据访问层
 */
@Repository
public interface PermissionDAO {

    PermissionPO get(@Param("permId") int permId);

    PermissionPO getByAppIdSign(@Param("appId") int appId, @Param("sign") String sign);

    List<PermissionPO> listByAppId(@Param("appId") int appId);

    List<PermissionPO> listByAppIdState(@Param("appId") int appId, @Param("state") int state);

    int insert(PermissionPO permission);

    int update(PermissionPO permission);

    int updateState(@Param("permId") int permId, @Param("state") int state);

    int delete(@Param("permId") int permId);

}
