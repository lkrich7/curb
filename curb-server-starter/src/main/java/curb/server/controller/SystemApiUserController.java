package curb.server.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserState;
import curb.core.util.StringUtil;
import curb.server.bo.Pagination;
import curb.server.converter.OptionVOConverter;
import curb.server.enums.SystemRole;
import curb.server.po.RolePO;
import curb.server.service.RoleService;
import curb.server.service.UserRoleService;
import curb.server.service.UserService;
import curb.server.vo.OptionSelectVO;
import curb.server.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 用户管理API
 */
@RestController
@RequestMapping("/system/api/user/")
public class SystemApiUserController {

    private static final int MAX_PAGE_SIZE = 200;
    private static final int DEFAULT_PAGE_SIZE = 15;

    private final UserService userService;

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    public SystemApiUserController(UserService userService,
                                   RoleService roleService,
                                   UserRoleService userRoleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    /**
     * 用户列表
     *
     * @param keyword 搜索关键词
     * @param type    用户类型
     * @param pn      分页页号
     * @param ps      分页大小
     * @param group   项目组对象
     * @return
     */
    @GetMapping("list")
    public ApiResult<PaginationVO<User>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer type,
                                              @RequestParam(required = false) Integer state,
                                              @RequestParam(required = false, defaultValue = "1") Integer pn,
                                              @RequestParam(required = false, defaultValue = "15") Integer ps,
                                              Group group) {
        if (ps < 1 || ps > MAX_PAGE_SIZE) {
            ps = DEFAULT_PAGE_SIZE;
        }
        keyword = StringUtil.trimToNull(keyword);
        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("groupId", group.getGroupId());
        paramMap.put("keyword", keyword);
        paramMap.put("type", type);
        paramMap.put("state", state);

        Pagination<User> results = userService.search(paramMap, pn, ps);
        PaginationVO<User> data = results.toVO(e -> e);

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 获取指定用户信息，用于编辑用户信息
     *
     * @param userId 用户ID
     * @return
     */
    @GetMapping("get")
    public ApiResult<User> getForEdit(@RequestParam int userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ErrorEnum.NOT_FOUND.toApiResult();
        }
        return ErrorEnum.SUCCESS.toApiResult(user);
    }

    /**
     * 创建用户
     *
     * @param username 用户名
     * @param name     用户姓名
     * @param type     用户类型
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createUser(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam int type
    ) {
        User user = checkParam(null, name, username, type);
        userService.create(user);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 更新用户信息
     *
     * @param userId   用户ID
     * @param name     用户姓名
     * @param username 用户名
     * @param type     用户类型
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updateUser(@RequestParam int userId,
                                      @RequestParam String name,
                                      @RequestParam String username,
                                      @RequestParam int type
    ) {
        User user = checkParam(userId, name, username, type);
        userService.update(user);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 停用用户
     *
     * @param userId 用户ID
     * @return
     */
    @PostMapping("block")
    public ApiResult<Void> blockUser(@RequestParam int userId) {
        userService.updateState(userId, UserState.BLOCKED);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 重新启用用户
     *
     * @param userId 用户
     * @return
     */
    @PostMapping("recover")
    public ApiResult<Void> recoverUser(@RequestParam int userId) {
        userService.updateState(userId, UserState.OK);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 永久删除用户
     *
     * @param userId 用户ID
     * @return
     */
    @PostMapping("delete")
    public ApiResult<Void> deleteUser(@RequestParam int userId) {
        userService.delete(userId);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取指定用户的角色配置，用于为该用户配置角色
     *
     * @param userId 用户ID
     * @return
     */
    @GetMapping("role/list")
    public ApiResult<OptionSelectVO> listRoleForEdit(@RequestParam int userId,
                                                     Group group) {
        Integer groupId = group.getGroupId();
        userService.checkUserExisted(userId);

        List<RolePO> groupRoles = roleService.listByGroupIdWithSystem(groupId);
        Set<Integer> userRoleIds = userRoleService.listRoleIdByUserId(userId, groupId, groupRoles);

        OptionSelectVO data = OptionVOConverter.convert(groupRoles, userRoleIds);

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 保存用户的角色配置
     *
     * @param userId 用户ID
     * @param roleId 配置的角色ID集合
     * @param group  项目组
     * @return
     */
    @PostMapping("role/save")
    public ApiResult<Void> saveRole(@RequestParam int userId,
                                    @RequestParam Set<Integer> roleId,
                                    Group group, User operator) {

        Set<Integer> normalRoleIds = new TreeSet<>();
        Set<SystemRole> systemRoleIds = EnumSet.noneOf(SystemRole.class);
        for (Integer r : roleId) {
            SystemRole systemRole = SystemRole.getByRoleId(r);
            if (systemRole != null) {
                systemRoleIds.add(systemRole);
            } else {
                normalRoleIds.add(r);
            }
        }

        userRoleService.saveUserRoles(userId, group.getGroupId(), normalRoleIds, systemRoleIds, operator.getUserId());
        return ErrorEnum.SUCCESS.toApiResult();
    }

    private User checkParam(Integer userId, String name, String username, int type) {
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setUsername(username);
        user.setType(type);
        return user;
    }
}
