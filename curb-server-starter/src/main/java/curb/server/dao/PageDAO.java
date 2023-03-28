package curb.server.dao;

import curb.server.po.PagePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 页面表数据访问层
 */
@Repository
public interface PageDAO {

    PagePO get(@Param("pageId") int pageId);

    PagePO getByAppIdPath(@Param("appId") int appId, @Param("path") String path);

    int countByCondition(@Param("appId") int appId);

    List<PagePO> listByCondition(@Param("appId") int appId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    int insert(PagePO page);

    int update(PagePO page);

    int updateVersion(@Param("pageId") int pageId, @Param("newVersion") int newVer, @Param("oldVersion") int oldVers);

    int updateState(@Param("pageId") int pageId, @Param("state") int state);

    int delete(@Param("pageId") int pageId);

    int deleteByAppId(@Param("appId") int appId);

}
