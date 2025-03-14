package curb.server.controller;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.Group;
import curb.core.util.StringUtil;
import curb.server.converter.GroupVOConverter;
import curb.server.po.GroupPO;
import curb.server.service.GroupService;
import curb.server.util.CurbServerUtil;
import curb.server.vo.GroupVO;
import curb.server.vo.PaginationVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 项目组管理API
 */
@RestController
@RequestMapping("/system/api/group/")
public class SystemApiGroupController {

    private final GroupService groupService;

    public SystemApiGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 项目组列表
     *
     * @param group 当前项目组对象
     * @return
     */
    @GetMapping(value = "list")
    public ApiResult<PaginationVO<GroupVO>> listGroup(Group group) {
        List<GroupVO> list;
        if (CurbServerUtil.isSystemGroupId(group.getGroupId())) {
            // 当前在系统项目组时，列出全部项目组
            list = GroupVOConverter.convert(groupService.list());
        } else {
            GroupVO groupVO = GroupVOConverter.convert(groupService.getById(group.getGroupId()));
            list = Collections.singletonList(groupVO);
        }
        PaginationVO<GroupVO> data = new PaginationVO<>(list, list.size());
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 获取项目组信息，用于项目组信息编辑
     *
     * @param groupId 指定的项目组ID，如缺省则获取当前项目组
     * @param group   当前项目组
     * @return
     */
    @GetMapping("get")
    public ApiResult<GroupVO> getForEdit(@RequestParam(required = false) Integer groupId, Group group) {
        groupId = CurbServerUtil.checkGroupId(groupId, group);
        GroupPO po = groupService.getById(groupId);
        GroupVO data = GroupVOConverter.convert(po);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 新建项目组
     *
     * @param name 项目组名称
     * @param url  项目组标识
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createGroup(@RequestParam String name,
                                       @RequestParam String url,
                                       Group group
    ) {
        GroupPO groupPO = checkParam(null, url, name, group);
        groupService.create(groupPO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 修改项目组
     *
     * @param groupId 项目组ID
     * @param name    项目组名称
     * @param url     项目组网址
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updateGroup(@RequestParam int groupId,
                                       @RequestParam String name,
                                       @RequestParam String url,
                                       Group group) {
        GroupPO groupPO = checkParam(groupId, url, name, group);
        groupService.update(groupPO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    private GroupPO checkParam(Integer groupId, String url, String name, Group group) {
        CurbServerUtil.checkGroupId(groupId, group);

        url = StringUtil.trimToNull(url);
        name = StringUtil.trimToNull(name);

        if (StringUtil.isBlank(name)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("项目组名称不能为空");
        }
        if (StringUtil.isBlank(url)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("项目组网址不能为空");
        }
        GroupPO ret = new GroupPO();
        ret.setGroupId(groupId);
        ret.setUrl(url);
        ret.setName(name);
        return ret;
    }

}
