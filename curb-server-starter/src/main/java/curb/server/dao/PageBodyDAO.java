package curb.server.dao;

import curb.server.po.PageBodyPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 页面表数据访问层
 */
@Repository
public interface PageBodyDAO {

    PageBodyPO get(@Param("pageId") int pageId, @Param("version") int version);

    int countByPageId(@Param("pageId") int pageId);

    List<PageBodyPO> listByPageId(@Param("pageId") int pageId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    int insert(PageBodyPO page);

    int delete(@Param("pageId") int pageId, @Param("version") int version);

    int deleteByPageId(@Param("pageId") int pageId);
}
