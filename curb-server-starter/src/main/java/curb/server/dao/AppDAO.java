package curb.server.dao;

import curb.server.po.AppPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用基本信息表数据访问层
 */
@Repository
public interface AppDAO {

    AppPO get(@Param("appId") int appId);

    AppPO getByUrl(@Param("url") String url);

    /**
     * @param groupId 项目组ID
     * @param state   状态值，为空则取全部状态
     * @return
     */
    List<AppPO> listByGroupId(@Param("groupId") int groupId, @Param("state") Integer state);

    int insert(AppPO app);

    int update(AppPO app);

    int updateState(@Param("appId") int appId, @Param("state") int state);

    int delete(@Param("appId") int appId);

}
