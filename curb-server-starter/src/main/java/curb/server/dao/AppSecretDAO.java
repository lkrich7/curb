package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 应用密钥表数据访问层
 */
@Repository
public interface AppSecretDAO {

    String get(int appId);

    int insert(@Param("appId") int appId, @Param("secret") String secret);

    int update(@Param("appId") int appId, @Param("secret") String secret);

    int delete(int appId);
}
