package curb.server.service;

import curb.core.ErrorEnum;
import curb.server.enums.OpType;
import curb.server.dao.OpLogDAO;
import curb.server.po.OpLogPO;
import curb.server.dto.Paged;
import curb.core.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 */
@Service
public class OpLogService {

    @Autowired
    OpLogDAO opLogDAO;

    public boolean create(OpLogPO opLogPO) {
        int change = opLogDAO.insert(opLogPO);
        return change == 1;
    }

    public Paged<OpLogPO> search(Map<String, Object> paramMap, int pageNo, int pageSize) {
        int totalCount = opLogDAO.countByParam(paramMap);
        int totalPage = new Paged<>(pageNo, pageSize, totalCount).getTotalPage();
        if (pageNo > totalPage) {
            pageNo = totalPage <= 0 ? 1 : totalPage;
        }
        paramMap.put("start", pageSize * (pageNo - 1));
        paramMap.put("limit", pageSize);
        List<OpLogPO> results = opLogDAO.getByParam(paramMap);
        Paged<OpLogPO> paged = new Paged<>(pageNo, pageSize, totalCount);
        paged.setPageList(results);
        return paged;
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
