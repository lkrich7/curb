package curb.server.service;

import curb.server.bo.Pagination;
import curb.server.dao.OpLogDAO;
import curb.server.po.OpLogPO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 操作日志服务
 */
@Service
public class OpLogService {

    private final OpLogDAO opLogDAO;

    public OpLogService(OpLogDAO opLogDAO) {
        this.opLogDAO = opLogDAO;
    }

    public void create(OpLogPO opLogPO) {
        opLogDAO.insert(opLogPO);
    }

    public Pagination<OpLogPO> search(Map<String, Object> paramMap, int pn, int ps) {
        int totalCount = opLogDAO.countByCondition(paramMap);
        int totalPage = new Pagination<>(pn, ps, totalCount).pages();
        if (pn > totalPage) {
            pn = totalPage <= 0 ? 1 : totalPage;
        }
        paramMap.put("start", ps * (pn - 1));
        paramMap.put("limit", ps);
        List<OpLogPO> results = opLogDAO.listByCondition(paramMap);
        Pagination<OpLogPO> pagination = new Pagination<>(pn, ps, totalCount);
        pagination.setItems(results);
        return pagination;
    }

}
