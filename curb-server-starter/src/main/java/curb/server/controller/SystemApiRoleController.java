package curb.server.controller;

import com.google.common.base.Joiner;
import curb.core.ApiResult;
import curb.server.vo.OptionVO;
import curb.server.vo.OptionSelectVO;
import curb.server.vo.PaginationVO;
import curb.server.vo.RoleVO;
import curb.server.vo.RoleUserListVO;
import curb.server.po.PermissionPO;
import curb.server.po.RolePO;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.ErrorEnum;
import curb.server.enums.RoleState;
import curb.server.service.AppService;
import curb.server.service.PermissionService;
import curb.server.service.RolePermissionService;
import curb.server.service.RoleService;
import curb.server.service.UserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 角色管理API
 */
@RestController
@RequestMapping("/system/api/role/")
public class SystemApiRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AppService appService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 角色列表
     *
     * @param group 项目组对象
     * @return
     */
    @GetMapping("list")
    public ApiResult<PaginationVO<RoleVO>> listRole(Group group) {
        List<RolePO> poList = roleService.listAllByGroupId(group.getGroupId());
        List<RoleVO> rows = RoleVO.fromPO(poList);
        PaginationVO<RoleVO> data = new PaginationVO<>(rows, rows.size());

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 获取指定角色信息，用于角色信息编辑
     *
     * @param roleId
     * @param group
     * @return
     */
    @GetMapping("get")
    public ApiResult<RoleVO> getForEdit(@RequestParam int roleId, Group group) {
        RolePO role = roleService.checkRole(roleId, group.getGroupId());
        RoleVO data = RoleVO.fromPO(role);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 新建角色
     *
     * @param sign        角色标识
     * @param name        角色名称
     * @param description 角色说明
     * @param group       项目组对象
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createRole(@RequestParam String sign,
                                      @RequestParam String name,
                                      @RequestParam String description,
                                      Group group) {
        RolePO roleVo = checkParam(null, sign, name, description, group);
        roleVo.setState(RoleState.ENABLED.getCode());
        roleService.create(roleVo);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 修改角色
     *
     * @param roleId      角色ID
     * @param sign        角色标识
     * @param name        角色名称
     * @param description 角色说明
     * @param group       项目组对象
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updateRole(@RequestParam int roleId,
                                      @RequestParam String sign,
                                      @RequestParam String name,
                                      @RequestParam String description,
                                      Group group) {
        RolePO roleVo = checkParam(roleId, sign, name, description, group);
        roleService.update(roleVo);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 停用角色
     *
     * @param roleId 角色ID
     * @param group  项目组对象
     * @return
     */
    @PostMapping("enable")
    public ApiResult<Void> enableRole(@RequestParam Integer roleId,
                                      Group group) {
        roleService.updateState(roleId, group.getGroupId(), RoleState.ENABLED);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 启用角色
     *
     * @param roleId 角色ID
     * @param group  项目组对象
     * @return
     */
    @PostMapping("disable")
    public ApiResult<Void> disableRole(@RequestParam Integer roleId,
                                       Group group) {
        roleService.updateState(roleId, group.getGroupId(), RoleState.DISABLED);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @param group  项目组对象
     * @return
     */
    @PostMapping("delete")
    public ApiResult<Void> deleteRole(@RequestParam Integer roleId,
                                      Group group) {
        roleService.delete(roleId, group.getGroupId());
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取指定角色的用户
     *
     * @param roleId 角色
     * @return
     */
    @GetMapping("user/list")
    public ApiResult<RoleUserListVO> listUser(@RequestParam int roleId,
                                              Group group) {

        List<User> users = userRoleService.listUserByRoleId(roleId, group.getGroupId());

        RoleUserListVO data = new RoleUserListVO();
        data.setRoleId(roleId);
        data.setUserId(users);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 保存用户的角色配置
     *
     * @param roleId 用户ID
     * @param roleId 配置的角色ID集合
     * @param group  项目组
     * @return
     */
    @PostMapping("user/save")
    public ApiResult<Void> saveUser(@RequestParam int roleId,
                                    @RequestParam Set<Integer> userId,
                                    Group group, User operator) {
        userRoleService.saveRoleUsers(roleId, group.getGroupId(), userId, operator.getUserId());
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取指定角色在指定应用上的权限配置
     *
     * @param roleId 角色ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @GetMapping("permission/list")
    public ApiResult<OptionSelectVO> listPermissionForEdit(@RequestParam int roleId,
                                                           @RequestParam(required = false) Integer appId,
                                                           App app, Group group) {
        roleService.checkRole(roleId, group.getGroupId());
        appId = appService.checkApp(appId, app, group).getAppId();

        List<PermissionPO> poList = permissionService.listByAppId(appId);
        Set<Integer> permIds = rolePermissionService.listPermId(roleId, poList);

        OptionSelectVO data = new OptionSelectVO();
        data.setOptions(toOptions(poList));
        data.setValue(Joiner.on(',').skipNulls().join(permIds));

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 保存角色的应用权限配置
     *
     * @param roleId 角色ID
     * @param appId  应用ID
     * @param permId 权限ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @PostMapping("permission/save")
    public ApiResult<Void> saveRole(@RequestParam int roleId,
                                    @RequestParam(required = false) Integer appId,
                                    @RequestParam Set<Integer> permId,
                                    App app, Group group) {
        roleService.checkRole(roleId, group.getGroupId());
        appId = appService.checkApp(appId, app, group).getAppId();

        rolePermissionService.saveRolePermissions(roleId, appId, permId);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    private RolePO checkParam(Integer roleId, String sign, String name, String description, Group group) {
        name = StringUtils.trimToNull(name);
        sign = StringUtils.trimToNull(sign);
        description = StringUtils.trimToNull(description);

        if (name == null || sign == null || description == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException();
        }

        RolePO roleVo = new RolePO();
        roleVo.setRoleId(roleId);
        roleVo.setGroupId(group.getGroupId());
        roleVo.setSign(sign);
        roleVo.setName(name);
        roleVo.setDescription(description);
        return roleVo;
    }

    private Collection<OptionVO> toOptions(List<PermissionPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        List<OptionVO> ret = new ArrayList<>(poList.size());
        for (PermissionPO po : poList) {
            String label = String.format("%s(%s)", po.getName(), po.getSign());
            String value = String.valueOf(po.getPermId());
            ret.add(new OptionVO(label, value));
        }
        return ret;
    }
}
