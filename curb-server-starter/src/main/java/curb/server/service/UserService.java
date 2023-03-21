package curb.server.service;

import curb.core.ErrorEnum;
import curb.core.model.UserState;
import curb.server.dao.UserDAO;
import curb.server.bo.Pagination;
import curb.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserRoleService userRoleService;

    public Pagination<User> search(Map<String, Object> paramMap, Integer pageNo, Integer pageSize) {
        int totalCount = userDAO.countByCondition(paramMap);
        Pagination<User> pagination = new Pagination<>(pageNo, pageSize, totalCount);
        if (pageNo < 1 || pageNo > pagination.pages()) {
            return pagination;
        }
        paramMap.put("start", pagination.offset());
        paramMap.put("limit", pageSize);

        List<User> results = userDAO.listByCondition(paramMap);
        pagination.setRows(results);
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

    @Transactional(rollbackFor = Exception.class)
    public void create(User user) {
        if (userDAO.getByUsername(user.getUsername()) != null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户名已存在");
        }
        user.setState(UserState.OK.getCode());
        int rows = userDAO.insert(user);
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        User existed = userDAO.get(user.getUserId());
        if (existed == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户不存在");
        }
        if (!existed.getUsername().equals(user.getUsername())) {
            User existedUsername = userDAO.getByUsername(user.getUsername());
            if (existedUsername != null && !existedUsername.getUserId().equals(existed.getUserId())) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("用户名已存在");
            }
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

}
