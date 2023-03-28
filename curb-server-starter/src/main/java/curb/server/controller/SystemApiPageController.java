package curb.server.controller;

import curb.core.AccessLevel;
import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.annotation.CurbMethod;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.server.bo.Pagination;
import curb.server.converter.AppVOConverter;
import curb.server.enums.PageState;
import curb.server.enums.PageType;
import curb.server.page.CurbPage;
import curb.server.po.AppPO;
import curb.server.po.PageBodyPO;
import curb.server.po.PagePO;
import curb.server.service.AppService;
import curb.server.service.PageService;
import curb.server.service.UserService;
import curb.server.vo.AppVO;
import curb.server.vo.PageBodyEditVO;
import curb.server.vo.PageBodyHistoryVO;
import curb.server.vo.PageEditVO;
import curb.server.vo.PageListVO;
import curb.server.vo.PaginationVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 页面管理API
 */
@RestController
@RequestMapping("/system/api/page/")
public class SystemApiPageController {

    private AppService appService;

    private PageService pageService;

    private UserService userService;

    @Autowired
    public SystemApiPageController(AppService appService, PageService pageService, UserService userService) {
        this.appService = appService;
        this.pageService = pageService;
        this.userService = userService;
    }

    /**
     * 页面列表
     *
     * @param appId 应用ID
     * @param app   当前应用对象
     * @param group 当前项目组对象
     * @return
     */
    @GetMapping("list")
    public ApiResult<PageListVO> list(@RequestParam(required = false) Integer appId,
                                      @RequestParam(required = false, defaultValue = "1") Integer pn,
                                      @RequestParam(required = false, defaultValue = "15") Integer ps,
                                      App app, Group group) {
        AppPO appPO = appService.checkApp(appId, app, group);

        appId = appPO.getAppId();
        Pagination<PagePO> poList = pageService.paginationListPage(appId, pn, ps);

        AppVO appVO = AppVOConverter.fromPO(appPO);
        PageListVO data = new PageListVO(appVO);
        poList.fillVO(PagePO::toPageListItemVO, data);

        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 创建页面
     *
     * @param appId 应用ID
     * @param vo    页面配置信息编辑对象
     * @param app   当前应用对象
     * @param group 当前项目组对象
     * @param user  当前操作用户
     * @return
     */
    @PostMapping("create")
    public ApiResult<Void> createPage(@RequestParam(required = false) Integer appId,
                                      @RequestBody PageEditVO vo,
                                      App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO pagePO = checkParams(vo, appId);
        pagePO.setCreateUserId(user.getUserId());
        pagePO.setUpdateUserId(user.getUserId());
        pageService.create(pagePO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 配置页面-获取页面配置信息
     *
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @GetMapping("config/get")
    public ApiResult<PageEditVO> getForEdit(@RequestParam(required = false) Integer appId,
                                            @RequestParam int pageId,
                                            App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO po = pageService.checkPage(pageId, appId);

        PageEditVO vo = toVO(po);
        return ErrorEnum.SUCCESS.toApiResult(vo);
    }

    /**
     * 配置页面 - 更新页面配置信息
     *
     * @param appId 应用ID
     * @param vo    页面信息编辑对象
     * @param app   当前应用对象
     * @param group 当前项目组对象
     * @param user  当前操作用户
     * @return
     */
    @PostMapping(value = "config/update")
    public ApiResult<Void> updatePage(@RequestParam(required = false) Integer appId,
                                      PageEditVO vo,
                                      App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO pagePO = checkParams(vo, appId);
        pagePO.setUpdateUserId(user.getUserId());
        pageService.update(pagePO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 编辑页面 - 获取页面内容编辑对象
     *
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @GetMapping("body/get")
    public ApiResult<PageBodyEditVO> getBodyForEdit(@RequestParam(required = false) Integer appId,
                                                    @RequestParam int pageId,
                                                    @RequestParam(required = false) Integer version,
                                                    App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO pagePO = pageService.checkPage(pageId, appId);
        PageBodyPO bodyPO = pageService.getBody(pageId, pagePO.getVersion());
        PageBodyEditVO data = new PageBodyEditVO();
        data.setPageId(pageId);
        data.setName(pagePO.getName());
        data.setPath(pagePO.getPath());
        data.setType(pagePO.getType());
        data.setVersion(pagePO.getVersion());
        if (bodyPO != null) {
            data.setBody(bodyPO.getBody());
        }
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * @param appId 应用ID
     * @param vo    页面内容编辑对象
     * @param app   当前应用对象
     * @param group 当前项目组对象
     * @param user  当前操作用户
     * @return
     */
    @PostMapping("body/update")
    public ApiResult<Void> saveBody(@RequestParam(required = false) Integer appId,
                                    PageBodyEditVO vo,
                                    App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();
        pageService.saveBody(vo, appId, user.getUserId());
        return ErrorEnum.SUCCESS.toApiResult();
    }

    @GetMapping("body/history/list")
    public ApiResult<PaginationVO<PageBodyHistoryVO>> listBodyHistory(@RequestParam(required = false) Integer appId,
                                                                      @RequestParam int pageId,
                                                                      @RequestParam(required = false, defaultValue = "1") Integer pn,
                                                                      @RequestParam(required = false, defaultValue = "15") Integer ps,
                                                                      App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        pageService.checkPage(pageId, appId);
        Pagination<PageBodyPO> pagination = pageService.pagedListBodyHistory(pageId, pn, ps);

        Set<Integer> userIds = pagination.getItems().stream()
                .map(PageBodyPO::getUserId)
                .collect(Collectors.toSet());
        Map<Integer, User> userMap = userService.listByUserIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, e -> e));
        PaginationVO<PageBodyHistoryVO> data = pagination.toVO(e -> toVO(e, userMap));
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    @PostMapping("body/history/rollback")
    public ApiResult<Void> rollbackPageBody(@RequestParam(required = false) Integer appId,
                                            @RequestParam int pageId,
                                            @RequestParam int version,
                                            App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        pageService.checkPage(pageId, appId);
        pageService.rollback(pageId, version);
        return ErrorEnum.SUCCESS.toApiResult();
    }


    private PageBodyHistoryVO toVO(PageBodyPO po, Map<Integer, User> userMap) {
        PageBodyHistoryVO ret = new PageBodyHistoryVO();
        ret.setPageId(po.getPageId());
        ret.setVersion(po.getVersion());
        ret.setUpdateTime(po.getUpdateTime());
        ret.setUser(userMap.get(po.getUserId()));
        ret.setBody(po.getBody());
        return ret;
    }

    /**
     * 启用指定的应用页面
     *
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @param user   当前操作用户
     * @return
     */
    @PostMapping("enable")
    public ApiResult<Void> enablePage(@RequestParam(required = false) Integer appId,
                                      @RequestParam int pageId,
                                      App app, Group group, User user) {

        appId = appService.checkApp(appId, app, group).getAppId();

        pageService.updateState(pageId, appId, PageState.ENABLED);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 停用指定的应用页面
     *
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @param user   当前操作用户
     * @return
     */
    @PostMapping("disable")
    public ApiResult<Void> disablePage(@RequestParam(required = false) Integer appId,
                                       @RequestParam int pageId,
                                       App app, Group group, User user) {

        appId = appService.checkApp(appId, app, group).getAppId();

        pageService.updateState(pageId, appId, PageState.DISABLED);

        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组
     * @param user   当前操作用户
     * @return
     */
    @PostMapping("delete")
    public ApiResult<Void> deletePage(@RequestParam(required = false) Integer appId,
                                      @RequestParam int pageId,
                                      App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();

        pageService.delete(pageId, appId);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取页面结构对象
     *
     * @param url     页面url
     * @param version 页面版本号
     * @param app     当前应用对象
     * @return
     */
    @GetMapping("schema/get")
    @CurbMethod(level = AccessLevel.LOGIN)
    public ApiResult<Object> getPageSchema(@RequestParam String url,
                                           @RequestParam(required = false) Integer version,
                                           App app) {
        CurbPage page = pageService.getPage(app.getAppId(), url, version);
        if (page == null) {
            return ErrorEnum.NOT_FOUND.toApiResult();
        }
        return ErrorEnum.SUCCESS.toApiResult(page.toAmisSchema());
    }

    private PagePO checkParams(PageEditVO vo, Integer appId) {
        String path = checkAndFixPath(vo.getPath());

        String name = StringUtils.trimToNull(vo.getName());
        if (name == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("页面名称不能为空");
        }

        PageType pageType = PageType.valueOfCode(vo.getType());
        if (pageType == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("页面类型错误");
        }
        AccessLevel accessLevel = AccessLevel.valueOfCode(vo.getAccessLevel());
        if (accessLevel == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("页面访问级别错误");
        }
        String sign = "";
        if (accessLevel == AccessLevel.PERMISSION) {
            sign = StringUtils.trimToEmpty(vo.getSign());
            if (sign.isEmpty()) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("页面访问权限标识不能为空");
            }
        }

        PagePO ret = new PagePO();
        ret.setPageId(vo.getPageId());
        ret.setAppId(appId);
        ret.setPath(path);
        ret.setName(name);
        ret.setType(pageType.getCode());
        ret.setAccessLevel(accessLevel.getCode());
        ret.setSign(sign);
        ret.setVersion(0);
        ret.setState(PageState.ENABLED.getCode());
        return ret;

    }

    private String checkAndFixPath(String path) {
        String ret = CurbPage.parsePath(path);
        if (ret == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("页面地址路径格式错误");
        }
        return ret;
    }

    private static PageEditVO toVO(PagePO po) {
        if (po == null) {
            return null;
        }
        PageEditVO ret = new PageEditVO();
        ret.setPageId(po.getPageId());
        ret.setName(po.getName());
        ret.setPath(po.getPath());
        ret.setType(po.getType());
        ret.setAccessLevel(po.getAccessLevel());
        ret.setSign(po.getSign());
        BeanUtils.copyProperties(po, ret);
        return ret;
    }
}
