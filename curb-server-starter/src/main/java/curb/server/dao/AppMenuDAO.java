package curb.server.dao;

import curb.server.po.AppMenuPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 应用菜单表数据库访问层
 */
@Repository
public interface AppMenuDAO {

    AppMenuPO get(@Param("appId") int appId, @Param("version") int version);

    AppMenuPO getLatest(@Param("appId") int appId);

    int insert(AppMenuPO po);

    int delete(@Param("appId") int appId);
}
