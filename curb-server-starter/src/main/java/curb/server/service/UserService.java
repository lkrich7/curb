package curb.server.service;

import curb.core.ErrorEnum;
import curb.core.model.UserState;
import curb.server.dao.UserDAO;
import curb.server.dto.Paged;
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

    public Paged<User> search(Map<String, Object> paramMap, Integer pageNo, Integer pageSize) {
        int totalCount = userDAO.countByCondition(paramMap);
        Paged<User> paged = new Paged<>(pageNo, pageSize, totalCount);
        if (pageNo < 1 || pageNo > paged.getTotalPage()) {
            return paged;
        }
        paramMap.put("start", paged.offset());
        paramMap.put("limit", pageSize);

        List<User> results = userDAO.listByCondition(paramMap);
        paged.setPageList(results);
        return paged;
    }

    public User getById(int userId) {
        return userDAO.get(userId);
    }

    public User getByEmail(String email) {
        return userDAO.getByEmail(email);
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
        if (userDAO.getByEmail(user.getEmail()) != null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("用户邮箱已存在");
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
        if (!existed.getEmail().equals(user.getEmail())) {
            User existedEmail = userDAO.getByEmail(user.getEmail());
            if (existedEmail != null && !existedEmail.getUserId().equals(existed.getUserId())) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("用户邮箱已存在");
            }
        }
        existed.setEmail(user.getEmail());
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
