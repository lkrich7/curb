package curb.server.service;

import curb.core.ErrorEnum;
import curb.server.dao.GroupDAO;
import curb.server.dao.GroupSecretDAO;
import curb.server.po.AppPO;
import curb.server.po.GroupPO;
import curb.server.util.CurbServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
public class GroupService {

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private GroupSecretDAO groupSecretDAO;

    @Autowired
    private AppService appService;

    public GroupPO getById(Integer groupId) {
        return groupDAO.get(groupId);
    }

    public String getGroupSecret(Integer groupId) {
        if (groupId == null) {
            return null;
        }
        return groupSecretDAO.get(groupId);
    }

    public List<GroupPO> list() {
        return groupDAO.list();
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(GroupPO groupPO) {
        checkUrlConflict(null, groupPO.getUrl());
        int rows = groupDAO.insert(groupPO);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        // 生成项目组密钥
        groupSecretDAO.insert(groupPO.getGroupId(), CurbServerUtil.generateSecret());

        // 创建项目组默认应用
        AppPO appPO = new AppPO();
        appPO.setGroupId(groupPO.getGroupId());
        appPO.setName(groupPO.getName());
        appPO.setUrl(groupPO.getUrl());
        appService.create(appPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(GroupPO group) {
        checkUrlConflict(group.getGroupId(), group.getUrl());
        GroupPO existed = checkGroup(group.getGroupId());
        existed.setName(group.getName());
        existed.setUrl(group.getUrl());

        int row = groupDAO.update(existed);
        if (row != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        return true;
    }

    private GroupPO checkGroup(int groupId) {
        GroupPO group = groupDAO.get(groupId);

        if (group == null) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return group;
    }

    private void checkUrlConflict(Integer groupId, String url) {
        GroupPO existed = groupDAO.getByUrl(url);
        if (existed == null || existed.getGroupId().equals(groupId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("网址已存在");
    }
}
