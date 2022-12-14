package curb.server.page;

import curb.core.CurbDataProvider;
import curb.core.CurbAccessConfig;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.Menu;
import curb.core.model.PermissionResult;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.util.CurbUtil;
import curb.core.util.JsonUtil;
import curb.core.util.MenuUtil;
import curb.server.enums.AppState;
import curb.server.po.AppPO;
import curb.server.service.AppMenuService;
import curb.server.service.AppService;
import curb.server.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Curb 托管页面请求处理器类
 */
public class CurbPageRequestHandler extends AbstractController {

    private static final String CURB_PAGE_ATTRIBUTE = "CURB_PAGE";
    @Autowired
    private PageService pageService;
    @Autowired
    private CurbDataProvider curbDataProvider;
    @Autowired
    private AppService appService;
    @Autowired
    private AppMenuService appMenuService;
    private String logo = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" style=\"width: 30px; height: 30px;\" viewBox=\"0 0 1024 1024\" height=\"200\" width=\"200\"><path d=\"M42.666667 546.133333v-61.866666c2.133333-10.666667 2.133333-19.2 4.266666-32 19.2-142.933333 91.733333-253.866667 213.333334-332.8 106.666667-68.266667 224-91.733333 345.6-64 189.866667 42.666667 311.466667 162.133333 362.666666 354.133333 4.266667 21.333333 6.4 46.933333 12.8 72.533333v66.133334c0 2.133333-2.133333 6.4-2.133333 10.666666-12.8 119.466667-59.733333 217.6-142.933333 296.533334-115.2 108.8-251.733333 147.2-405.333334 121.6-185.6-32-332.8-172.8-375.466666-360.533334-8.533333-23.466667-10.666667-49.066667-12.8-70.4z m635.733333 317.866667s0 2.133333 2.133333 2.133333c19.2-12.8 40.533333-21.333333 57.6-34.133333 125.866667-83.2 198.4-249.6 170.666667-396.8C883.2 300.8 810.666667 206.933333 689.066667 153.6c-46.933333-21.333333-98.133333-32-153.6-19.2-46.933333 10.666667-76.8 40.533333-91.733334 83.2-14.933333 44.8-4.266667 81.066667 25.6 115.2 21.333333 25.6 46.933333 44.8 76.8 59.733333 36.266667 19.2 74.666667 36.266667 110.933334 57.6 51.2 29.866667 89.6 68.266667 110.933333 125.866667 27.733333 72.533333 14.933333 138.666667-17.066667 202.666667-21.333333 34.133333-44.8 61.866667-72.533333 85.333333zM317.866667 149.333333s-4.266667 2.133333-6.4 4.266667c-6.4 4.266667-12.8 6.4-19.2 12.8-108.8 72.533333-170.666667 174.933333-181.333334 302.933333-10.666667 104.533333 19.2 198.4 83.2 279.466667 40.533333 51.2 87.466667 91.733333 151.466667 106.666667 49.066667 10.666667 98.133333 14.933333 145.066667-6.4 64-27.733333 104.533333-110.933333 49.066666-181.333334-25.6-32-57.6-53.333333-91.733333-72.533333-32-17.066667-66.133333-32-98.133333-51.2-134.4-78.933333-147.2-230.4-83.2-334.933333 14.933333-23.466667 36.266667-40.533333 51.2-59.733334z\"></path></svg>";

    /**
     * 检查是否可以处理当前请求
     *
     * @param request 当前请求
     * @return 可以处理则返回本对象，否则返回null
     * @see CurbPageRequestHandlerMapping
     */
    public CurbPageRequestHandler checkHandler(HttpServletRequest request) {
        App app = curbDataProvider.getApp(request);
        if (app == null) {
            return null;
        }

        String path = request.getRequestURI();
        CurbPage page = pageService.getPage(app.getAppId(), path);
        request.setAttribute(CURB_PAGE_ATTRIBUTE, page);
        if (page == null) {
            return null;
        }
        return this;
    }

    /**
     * 解析当前请求的访问控制配置
     *
     * @param request 当前请求
     * @return 访问控制配置
     * @see curb.core.mvc.interceptor.CurbInterceptor
     * @see CurbAccessConfig
     */
    public CurbAccessConfig resolveAccessConfig(HttpServletRequest request) {
        CurbPage page = (CurbPage) request.getAttribute(CURB_PAGE_ATTRIBUTE);
        return page;
    }

    /**
     * 请求处理方法
     *
     * @param request
     * @param response
     * @return
     * @see org.springframework.web.servlet.mvc.AbstractController
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        CurbPage page = (CurbPage) request.getAttribute(CURB_PAGE_ATTRIBUTE);
        if (CurbPage.isViewPageUrl(request.getRequestURI())) {
            return buildAmisPage(page);
        }
        return buildAmisIndex(page, request);
    }

    private ModelAndView buildAmisIndex(CurbPage page, HttpServletRequest request) {
        Group group = CurbUtil.getGroup(request);
        App app = CurbUtil.getApp(request);
        User user = CurbUtil.getUser(request);

        List<Menu> appMenu = appMenuService.listWithSystemMenu(app.getAppId());
        UserAppPermissions userAppPermissions = getUserAppPermissions(user, app, group, request);
        List<Menu> userMenu = MenuUtil.buildUserMenu(appMenu, userAppPermissions);
        List<AmisMenuVo> curbMenu = new ArrayList<>(AmisMenuVo.fromMenuList(userMenu));

        // 为当前页添加一个不可见的菜单项，防止菜单中没有配置当前页面路由
        AmisMenuVo defaultRoute = new AmisMenuVo();
        defaultRoute.setUrl(page.getPath());
        defaultRoute.setVisible(false);
        curbMenu.add(defaultRoute);

        List<AppPO> appPOList = appService.listByGroupId(group.getGroupId(), AppState.ENABLED);
        List<AmisAppVo> apps = AmisAppVo.fromAppPoList(appPOList);

        String mainPage = app.getMainPage();
        if (pageService.getPage(app.getAppId(), mainPage) != null) {
            mainPage = CurbPage.asViewPage(mainPage);
        }

        ModelMap modelMap = new ModelMap();
        modelMap.put("mainPage", mainPage);
        modelMap.put("curbPage", page);
        modelMap.put("curbLogo", logo);
        modelMap.put("curbMenu", JsonUtil.toJSONString(curbMenu, true));
        modelMap.put("curbApps", JsonUtil.toJSONString(apps, true));
        return new ModelAndView("/index-amis", modelMap);
    }

    private ModelAndView buildAmisPage(CurbPage page) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("curbPage", page);
        modelMap.put("curbPageSchema", JsonUtil.toJSONString(page.toAmisSchema(), true));
        return new ModelAndView("/page-amis", modelMap);
    }

    private UserAppPermissions getUserAppPermissions(User user, App app, Group group, HttpServletRequest request) {
        PermissionResult permissionResult = CurbUtil.getPermissionResult(request);
        if (permissionResult != null) {
            return permissionResult.getUserAppPermissions();
        }
        return curbDataProvider.getUserAppPermissions(user, app, group);
    }
}
