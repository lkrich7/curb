package curb.server.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    private LoadingCache<Integer, Optional<GroupPO>> idGroupCatch = CacheBuilder.newBuilder()
            .maximumSize(100).expireAfterWrite(5, TimeUnit.SECONDS)
            .build(new CacheLoader<Integer, Optional<GroupPO>>() {
                @Override
                public Optional<GroupPO> load(Integer groupId) {
                    GroupPO groupDto = groupDAO.get(groupId);
                    if (groupDto == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(groupDto);
                    }
                }
            });

    private LoadingCache<String, Optional<GroupPO>> domainGroupCatch = CacheBuilder.newBuilder()
            .maximumSize(100).expireAfterWrite(5, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Optional<GroupPO>>() {
                @Override
                public Optional<GroupPO> load(String domain) {
                    GroupPO groupDto = groupDAO.getByDomain(domain);
                    if (groupDto == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(groupDto);
                    }
                }
            });

    public GroupPO getById(Integer groupId) {
        try {
            return idGroupCatch.get(groupId).orElse(null);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public GroupPO getByDomain(String domain) {
        try {
            return domainGroupCatch.get(domain).orElse(null);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
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
        checkDomainConflict(null, groupPO.getDomain());

        int rows = groupDAO.insert(groupPO);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }

        groupSecretDAO.insert(groupPO.getGroupId(), CurbServerUtil.generateSecret());

        AppPO appPO = new AppPO();
        appPO.setGroupId(groupPO.getGroupId());
        appPO.setDomain(groupPO.getDomain());
        appPO.setName(groupPO.getName());
        appPO.setDescription(groupPO.getName());
        appService.create(appPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(GroupPO group) {
        checkDomainConflict(group.getGroupId(), group.getDomain());
        GroupPO existed = checkGroup(group.getGroupId());
        existed.setName(group.getName());
        existed.setDomain(group.getDomain());

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

    private void checkDomainConflict(Integer groupId, String domain) {
        GroupPO existed = groupDAO.getByDomain(domain);
        if (existed == null || existed.getGroupId().equals(groupId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("域名已存在");
    }
}
