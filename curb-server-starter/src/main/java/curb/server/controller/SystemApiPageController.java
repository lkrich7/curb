package curb.server.controller;

import curb.core.AccessLevel;
import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.annotation.CurbMethod;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.User;
import curb.server.enums.PageState;
import curb.server.enums.PageType;
import curb.server.page.CurbPage;
import curb.server.po.AppPO;
import curb.server.po.PageBodyPO;
import curb.server.po.PagePO;
import curb.server.service.AppService;
import curb.server.service.PageService;
import curb.server.vo.AppVO;
import curb.server.vo.PageBodyEditVO;
import curb.server.vo.PageEditVO;
import curb.server.vo.PageListItemVO;
import curb.server.vo.PageListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 页面管理API
 */
@RestController
@RequestMapping("/system/api/page/")
public class SystemApiPageController {

    @Autowired
    private AppService appService;

    @Autowired
    private PageService pageService;

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
                                      App app, Group group) {
        AppPO appPO = appService.checkApp(appId, app, group);
        AppVO appVO = AppVO.fromPO(appPO);

        appId = appPO.getAppId();

        List<PagePO> poList = pageService.listEditables(appId);
        List<PageListItemVO> list = PageListItemVO.fromPO(poList);

        PageListVO data = new PageListVO(list, list.size(), appVO);
        return ErrorEnum.SUCCESS.toApiResult(data);
    }

    /**
     * 页面详情
     *
     * @param appId  应用ID
     * @param pageId 页面ID
     * @param app    当前应用对象
     * @param group  当前项目组对象
     * @return
     */
    @GetMapping("get")
    public ApiResult<PageEditVO> getForEdit(@RequestParam(required = false) Integer appId,
                                            @RequestParam int pageId,
                                            App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO po = pageService.checkEditablePage(pageId, appId);

        PageEditVO vo = PageEditVO.fromPO(po);
        return ErrorEnum.SUCCESS.toApiResult(vo);
    }

    /**
     * @param appId 应用ID
     * @param vo    页面信息编辑对象
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
        pageService.create(pagePO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * @param appId 应用ID
     * @param vo    页面信息编辑对象
     * @param app   当前应用对象
     * @param group 当前项目组对象
     * @param user  当前操作用户
     * @return
     */
    @PostMapping("update")
    public ApiResult<Void> updatePage(@RequestParam(required = false) Integer appId,
                                      @RequestBody PageEditVO vo,
                                      App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO pagePO = checkParams(vo, appId);
        pageService.update(pagePO);
        return ErrorEnum.SUCCESS.toApiResult();
    }

    /**
     * 获取页面内容编辑对象
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
                                                    App app, Group group) {
        appId = appService.checkApp(appId, app, group).getAppId();
        PagePO po = pageService.checkEditablePage(pageId, appId);
        PageBodyPO bodyPO = pageService.getBody(pageId, po.getVersion());
        PageBodyEditVO data = new PageBodyEditVO();
        data.setPageId(pageId);
        data.setVersion(po.getVersion());
        if (bodyPO != null) {
            bodyPO.setBody(bodyPO.getBody());
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
    @PostMapping("body/save")
    public ApiResult<Void> saveBody(@RequestParam(required = false) Integer appId,
                                    @RequestBody PageBodyEditVO vo,
                                    App app, Group group, User user) {
        appId = appService.checkApp(appId, app, group).getAppId();
        pageService.saveBody(vo, appId);


        return ErrorEnum.SUCCESS.toApiResult();
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
     * @param url 页面url
     * @param app 当前应用对象
     * @return
     */
    @GetMapping("schema/get")
    @CurbMethod(level = AccessLevel.LOGIN)
    public ApiResult<Object> getPageSchema(@RequestParam String url, App app) {
        CurbPage page = pageService.getPage(app.getAppId(), url);
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
        AccessLevel accessLevel = AccessLevel.valueOfCode(vo.getAccessLevel(), AccessLevel.PERMISSION);
        String sign = StringUtils.trimToEmpty(vo.getSign());

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

}
