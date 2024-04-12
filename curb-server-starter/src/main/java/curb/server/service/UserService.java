package curb.server.service;

import curb.core.ErrorEnum;
import curb.core.model.User;
import curb.core.model.UserState;
import curb.server.bo.Pagination;
import curb.server.dao.UserDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService {

    private final UserDAO userDAO;

    private final UserRoleService userRoleService;

    public UserService(UserDAO userDAO, UserRoleService userRoleService) {
        this.userDAO = userDAO;
        this.userRoleService = userRoleService;
    }

    /**
     * 分页查询接口
     *
     * @param paramMap
     * @param pn
     * @param ps
     * @return
     */
    public Pagination<User> search(Map<String, Object> paramMap, Integer pn, Integer ps) {
        int totalCount = userDAO.countByCondition(paramMap);
        Pagination<User> pagination = new Pagination<>(pn, ps, totalCount);
        if (pn < 1 || pn > pagination.pages()) {
            return pagination;
        }
        paramMap.put("start", pagination.offset());
        paramMap.put("limit", ps);

        List<User> results = userDAO.listByCondition(paramMap);
        pagination.setItems(results);
        return pagination;
    }

    public User getById(int userId) {
        return userDAO.get(userId);
    }

    public User getByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    public User checkUserExisted(int userId) {
        User user = getById(userId);
        UserState state = UserState.check(user);
        if (state == UserState.NOT_EXISTED) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户" + state.getName());
        }
        return user;
    }

    public List<User> listByUserIds(Collection<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userDAO.listByUserIds(userIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(User user) {
        checkUsernameExisted(user.getUsername(), null);
        user.setState(UserState.OK.getCode());
        int rows = userDAO.insert(user);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        User existed = checkUserExisted(user.getUserId());
        if (!existed.getUsername().equals(user.getUsername())) {
            checkUsernameExisted(user.getUsername(), user.getUserId());
        }
        existed.setUsername(user.getUsername());
        existed.setName(user.getName());
        existed.setType(user.getType());
        int rows = userDAO.update(user);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateState(int userId, UserState state) {
        checkUserExisted(userId);
        userDAO.updateState(userId, state.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer userId) {
        int change = userDAO.delete(userId);
        if (1 != change) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        userRoleService.deleteByUserId(userId);
    }

    private void checkUsernameExisted(String username, Integer userId) {
        User existed = userDAO.getByUsername(username);
        if (existed != null && !existed.getUserId().equals(userId)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户名已存在");
        }
    }

}
