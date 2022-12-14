package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 项目组密钥表数据访问层
 */
@Repository
public interface GroupSecretDAO {

    String get(int groupId);

    int insert(@Param("groupId") int groupId, @Param("secret") String secret);

    int update(@Param("groupId") int groupId, @Param("secret") String secret);

    int delete(int groupId);
}
