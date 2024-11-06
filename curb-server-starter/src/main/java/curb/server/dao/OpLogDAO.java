package curb.server.dao;

import curb.server.po.OpLogPO;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户操作日志表数据访问层
 */
@Repository
public interface OpLogDAO {

    int insert(OpLogPO opLogPO);

    int countByCondition(Map<String, Object> map);

    List<OpLogPO> listByCondition(Map<String, Object> map);
}
