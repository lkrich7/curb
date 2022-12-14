package curb.server.controller;

import curb.core.ApiResult;
import curb.server.vo.GroupVO;
import curb.server.vo.PaginationVO;
import curb.server.po.GroupPO;
import curb.core.model.Group;
import curb.core.ErrorEnum;
import curb.server.service.GroupService;
import curb.server.util.CurbServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/api/group/")
public class SystemApiGroupController {

    @Autowired
    private GroupService groupService;

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
            list = GroupVO.fromPO(groupService.list());
        } else {
            GroupVO groupVO = GroupVO.fromPO(groupService.getById(group.getGroupId()));
            list = new ArrayList<>(1);
            list.add(groupVO);
        }
        PaginationVO<GroupVO> data = new PaginationVO<>(list, list.size());
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 获取指定项目组信息，用于项目组信息编辑
     *
     * @param groupId
     * @param group
     * @return
     */
    @GetMapping("get")
    public ApiResult<GroupVO> getForEdit(@RequestParam(required = false) Integer groupId, Group group) {
        groupId = CurbServerUtil.checkGroupId(groupId, group);
        GroupPO po = groupService.getById(groupId);
        GroupVO data = GroupVO.fromPO(po);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 新建项目组
     *
     * @param domain       项目组标识
     * @param name         项目组名称
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createGroup(@RequestParam String domain,
                                       @RequestParam String name,
                                       Group group
    ) {
        GroupPO groupPO = checkParam(null, domain, name, group);
        groupService.create(groupPO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 修改项目组
     *
     * @param groupId      项目组ID
     * @param domain       项目组标识
     * @param name         项目组名称
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updateGroup(@RequestParam int groupId,
                                       @RequestParam String domain,
                                       @RequestParam String name,
                                       Group group) {
        GroupPO groupPO = checkParam(groupId, domain, name, group);
        groupService.update(groupPO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    private GroupPO checkParam(Integer groupId, String domain, String name, Group group) {
        CurbServerUtil.checkGroupId(groupId, group);

        domain = StringUtils.trimToNull(domain);
        name = StringUtils.trimToNull(name);

        if (StringUtils.isBlank(name)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("项目组名称不可为空");
        }
        if (StringUtils.isBlank(domain)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("项目组根域名不可为空");
        }
        GroupPO ret = new GroupPO();
        ret.setGroupId(groupId);
        ret.setDomain(domain);
        ret.setName(name);
        return ret;
    }
}
