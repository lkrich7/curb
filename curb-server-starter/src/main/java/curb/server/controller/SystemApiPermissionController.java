package curb.server.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.Permission;
import curb.core.util.StringUtil;
import curb.server.converter.AppVOConverter;
import curb.server.converter.OptionVOConverter;
import curb.server.converter.PermissionVOConverter;
import curb.server.enums.PermissionState;
import curb.server.po.AppPO;
import curb.server.po.PermissionPO;
import curb.server.po.RolePO;
import curb.server.service.AppService;
import curb.server.service.PermissionService;
import curb.server.service.RolePermissionService;
import curb.server.service.RoleService;
import curb.server.vo.AppVO;
import curb.server.vo.OptionSelectVO;
import curb.server.vo.PermissionListVO;
import curb.server.vo.PermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/system/api/permission/")
public class SystemApiPermissionController {

    @Autowired
    private AppService appService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 应用权限列表
     *
     * @param appId 应用ID
     * @param app   当前应用对象
     * @param group 当前项目组
     * @return
     */
    @GetMapping("list")
    public ApiResult<PermissionListVO> listPermission(
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        AppPO appPO = appService.checkApp(appId, app, group);
        appId = appPO.getAppId();

        AppVO appVO = AppVOConverter.convert(appPO);
        List<PermissionPO> permissions = permissionService.listByAppId(appId);

        PermissionListVO data = new PermissionListVO();
        data.setItems(PermissionVOConverter.convert(permissions));
        data.setTotal(permissions.size());
        data.setApp(appVO);

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 查询应用权限
     *
     * @param permId 权限ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @return
     */
    @GetMapping("get")
    public ApiResult<PermissionVO> getForEdit(
            @RequestParam int permId,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        PermissionPO permission = permissionService.checkPermission(permId, appId);
        PermissionVO data = PermissionVOConverter.convert(permission);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 创建应用权限
     *
     * @param name  权限名称
     * @param sign  权限标识
     * @param appId 应用ID
     * @param app   当前应用对象
     * @param group 当前项目组
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createPermission(
            @RequestParam String name,
            @RequestParam String sign,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        PermissionPO permission = checkParam(null, appId, name, sign);

        permissionService.create(permission);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 更新应用权限
     *
     * @param permId 权限ID
     * @param name   权限名称
     * @param sign   权限标识
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updatePermission(
            @RequestParam int permId,
            @RequestParam String name,
            @RequestParam String sign,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        PermissionPO permission = checkParam(permId, appId, name, sign);
        permissionService.update(permission);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 停用指定的应用权限
     *
     * @param permId 权限ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @return
     */
    @PostMapping("disable")
    public ApiResult<Void> disablePermission(
            @RequestParam Integer permId,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        permissionService.updateState(permId, appId, PermissionState.DISABLED);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 启用指定的应用权限
     *
     * @param permId 权限ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @return
     */
    @PostMapping("enable")
    public ApiResult<Void> enablePermission(
            @RequestParam Integer permId,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        permissionService.updateState(permId, appId, PermissionState.ENABLED);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 永久删除指定的应用权限，权限与角色的关联关系也将删除
     *
     * @param permId 权限ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @return
     */
    @PostMapping("delete")
    public ApiResult<Void> deletePermission(
            @RequestParam int permId,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();

        permissionService.delete(permId, appId);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取指定用户的角色配置，用于为该用户配置角色
     *
     * @param permId 权限ID
     * @param appId  应用ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @GetMapping("role/list")
    public ApiResult<OptionSelectVO> listRoleForEdit(
            @RequestParam int permId,
            @RequestParam(required = false) Integer appId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();
        permissionService.checkPermission(permId, appId);

        List<RolePO> roles = roleService.listByGroupId(group.getGroupId());
        Set<Integer> roleIds = rolePermissionService.listRoleId(permId);

        OptionSelectVO data = OptionVOConverter.convert(roles, roleIds);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 保存用户的角色配置
     *
     * @param permId 用户ID
     * @param roleId 配置的角色ID集合
     * @param group  项目组
     * @return
     */
    @PostMapping("role/save")
    public ApiResult<Void> saveRole(
            @RequestParam int permId,
            @RequestParam(required = false) Integer appId,
            @RequestParam Set<Integer> roleId,
            App app, Group group) {

        appId = appService.checkApp(appId, app, group).getAppId();
        permissionService.checkPermission(permId, appId);

        rolePermissionService.savePermissionRoles(permId, group.getGroupId(), roleId);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    private PermissionPO checkParam(Integer permId, int appId, String name, String sign) {
        name = StringUtil.trimToNull(name);

        if (StringUtil.isBlank(name)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("权限名称不能为空");
        }
        if (StringUtil.isBlank(sign)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("权限标识不能为空");
        }
        Permission p = Permission.build(sign);
        if (p == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("权限标识格式错误");
        }
        PermissionPO permission = new PermissionPO();
        permission.setPermId(permId);
        permission.setAppId(appId);
        permission.setSign(p.getSign());
        permission.setName(name);
        permission.setDescription(name);
        permission.setState(PermissionState.ENABLED.getCode());
        return permission;
    }

}
