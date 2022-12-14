package curb.server.dao;

import curb.core.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户信息表数据访问层
 */
@Repository
public interface UserDAO {

    User get(@Param("userId") int userId);

    User getByEmail(String email);

    int countByCondition(Map<String, Object> paramMap);

    List<User> listByUserIds(@Param("userIds") Collection<Integer> userIds);

    List<User> listByCondition(Map<String, Object> paramMap);

    int insert(User user);

    int update(User user);

    int updateState(@Param("userId") int userId, @Param("state") int state);

    int delete(@Param("userId") int userId);

}
