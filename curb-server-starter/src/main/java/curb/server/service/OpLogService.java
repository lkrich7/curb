package curb.server.service;

import curb.core.ErrorEnum;
import curb.core.util.JsonUtil;
import curb.server.bo.Pagination;
import curb.server.dao.OpLogDAO;
import curb.server.enums.OpType;
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


    public boolean create(OpLogPO opLogPO) {
        int change = opLogDAO.insert(opLogPO);
        return change == 1;
    }

    public Pagination<OpLogPO> search(Map<String, Object> paramMap, int pn, int ps) {
        int totalCount = opLogDAO.countByParam(paramMap);
        int totalPage = new Pagination<>(pn, ps, totalCount).pages();
        if (pn > totalPage) {
            pn = totalPage <= 0 ? 1 : totalPage;
        }
        paramMap.put("start", ps * (pn - 1));
        paramMap.put("limit", ps);
        List<OpLogPO> results = opLogDAO.getByParam(paramMap);
        Pagination<OpLogPO> pagination = new Pagination<>(pn, ps, totalCount);
        pagination.setItems(results);
        return pagination;
    }

    public void log(Integer userId, OpType opType, Map<String, Object> content) {
        OpLogPO log = new OpLogPO();
        log.setUserId(userId);
        log.setType(opType.getCode());
        log.setContent(JsonUtil.toJSONString(content));
        if (!create(log)) {
            throw ErrorEnum.SERVER_ERROR.toCurbException("操作日志写入数据库失败");
        }
    }
}
