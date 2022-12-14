package curb.server.dao;

import curb.server.po.GroupPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目组基本信息表数据访问层
 */
@Repository
public interface GroupDAO {

    GroupPO get(int groupId);

    GroupPO getByDomain(String domain);

    List<GroupPO> list();

    int insert(GroupPO group);

    int update(GroupPO group);

    int delete(int groupId);
}
