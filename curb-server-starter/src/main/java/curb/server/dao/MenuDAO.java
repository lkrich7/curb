package curb.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 应用菜单表数据库访问层
 */
@Repository
public interface MenuDAO {

    String get(int appId);

    int insert(@Param("appId") int appId, @Param("menu") String menu);

    int update(@Param("appId") int appId, @Param("menu") String menu);

    int delete(int appId);
}
