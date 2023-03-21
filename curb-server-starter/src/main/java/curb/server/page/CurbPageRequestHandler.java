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
import curb.server.configuration.CurbServerProperties;
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
    private CurbServerProperties properties;
    @Autowired
    private PageService pageService;
    @Autowired
    private CurbDataProvider curbDataProvider;
    @Autowired
    private AppService appService;
    @Autowired
    private AppMenuService appMenuService;
    private String logo = "<svg width=\"53.675px\" height=\"36.8px\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"223.1625 56.6 53.675 36.8\" style=\"background: rgb(58, 63, 81);\" preserveAspectRatio=\"xMidYMid\"><defs><filter id=\"editing-lego\" x=\"-100%\" y=\"-100%\" width=\"300%\" height=\"300%\"><feFlood flood-color=\"#8b8b8b\" result=\"f1\"></feFlood><feFlood flood-color=\"#ffffff\" result=\"f2\"></feFlood><feMorphology operator=\"dilate\" radius=\"3\" in=\"SourceAlpha\"></feMorphology><feConvolveMatrix order=\"5,5\" divisor=\"1\" result=\"cm1\" kernelMatrix=\"0 1 1 1 0  1 0 0 0 1  1 0 0 0 1  1 0 0 0 1  0 1 1 1 0\"></feConvolveMatrix><feComposite operator=\"in\" in=\"f1\" in2=\"cm1\" result=\"c1\"></feComposite><feMorphology operator=\"dilate\" radius=\"3\"></feMorphology><feConvolveMatrix order=\"5,5\" divisor=\"1\" result=\"cm2\" kernelMatrix=\"0 1 1 1 0  1 0 0 0 1  1 0 0 0 1  1 0 0 0 1  0 1 1 1 0\"></feConvolveMatrix><feComposite operator=\"in\" in=\"f2\" in2=\"cm2\" result=\"c2\"></feComposite><feMerge><feMergeNode in=\"c2\"></feMergeNode><feMergeNode in=\"c1\"></feMergeNode><feMergeNode in=\"SourceGraphic\"></feMergeNode></feMerge></filter></defs><g filter=\"url(#editing-lego)\"><g transform=\"translate(235.20000654459, 79.65000295639038)\"><path d=\"M8.40-6.54L7.14-6.54L7.14-6.54Q6.75-7.21 6.10-7.59L6.10-7.59L6.10-7.59Q5.46-7.97 4.66-7.97L4.66-7.97L4.66-7.97Q3.98-7.97 3.42-7.68L3.42-7.68L3.42-7.68Q2.85-7.39 2.45-6.91L2.45-6.91L2.45-6.91Q2.05-6.43 1.83-5.79L1.83-5.79L1.83-5.79Q1.61-5.16 1.61-4.45L1.61-4.45L1.61-4.45Q1.61-3.62 1.85-2.98L1.85-2.98L1.85-2.98Q2.10-2.34 2.49-1.91L2.49-1.91L2.49-1.91Q2.89-1.49 3.43-1.20L3.43-1.20L3.43-1.20Q3.98-0.92 4.66-0.92L4.66-0.92L4.66-0.92Q5.58-0.92 6.27-1.37L6.27-1.37L6.27-1.37Q6.96-1.82 7.44-2.84L7.44-2.84L8.47-2.36L8.47-2.36Q8.25-1.72 7.72-1.13L7.72-1.13L7.72-1.13Q7.18-0.54 6.40-0.18L6.40-0.18L6.40-0.18Q5.62 0.18 4.67 0.18L4.67 0.18L4.67 0.18Q3.76 0.18 3-0.17L3-0.17L3-0.17Q2.24-0.51 1.64-1.14L1.64-1.14L1.64-1.14Q1.05-1.78 0.74-2.64L0.74-2.64L0.74-2.64Q0.42-3.51 0.42-4.45L0.42-4.45L0.42-4.45Q0.42-5.40 0.74-6.25L0.74-6.25L0.74-6.25Q1.05-7.09 1.61-7.71L1.61-7.71L1.61-7.71Q2.18-8.34 2.96-8.70L2.96-8.70L2.96-8.70Q3.74-9.06 4.66-9.06L4.66-9.06L4.66-9.06Q5.47-9.06 6.06-8.83L6.06-8.83L6.06-8.83Q6.66-8.60 7.07-8.28L7.07-8.28L7.07-8.28Q7.48-7.96 7.78-7.58L7.78-7.58L8.40-7.58L8.40-6.54ZM13.08-5.06L13.08-6.07L15.04-6.07L15.04-1.01L15.71-1.01L15.71 0L13.95 0L13.95-0.56L13.95-0.56Q13.54-0.25 13.05-0.07L13.05-0.07L13.05-0.07Q12.57 0.11 12.02 0.11L12.02 0.11L12.02 0.11Q11.56 0.11 11.19-0.03L11.19-0.03L11.19-0.03Q10.82-0.16 10.56-0.44L10.56-0.44L10.56-0.44Q10.29-0.71 10.15-1.14L10.15-1.14L10.15-1.14Q10.01-1.57 10.01-2.16L10.01-2.16L10.01-5.06L9.19-5.06L9.19-6.07L11.15-6.07L11.15-2.29L11.15-2.29Q11.15-1.59 11.47-1.24L11.47-1.24L11.47-1.24Q11.79-0.88 12.40-0.88L12.40-0.88L12.40-0.88Q12.76-0.88 13.15-1.01L13.15-1.01L13.15-1.01Q13.55-1.14 13.90-1.37L13.90-1.37L13.90-5.06L13.08-5.06ZM21.70-6.11L21.70-4.79L20.59-4.79L20.59-5.16L20.59-5.16Q20.40-5.16 20.19-5.09L20.19-5.09L20.19-5.09Q19.99-5.02 19.82-4.87L19.82-4.87L19.82-4.87Q19.64-4.72 19.51-4.49L19.51-4.49L19.51-4.49Q19.38-4.25 19.32-3.94L19.32-3.94L19.32-1.01L20.35-1.01L20.35 0L17.34 0L17.34-1.01L18.18-1.01L18.18-5.06L17.34-5.06L17.34-6.07L19.28-6.07L19.28-5.23L19.28-5.23Q19.38-5.42 19.51-5.58L19.51-5.58L19.51-5.58Q19.65-5.74 19.81-5.86L19.81-5.86L19.81-5.86Q19.97-5.98 20.17-6.05L20.17-6.05L20.17-6.05Q20.36-6.12 20.59-6.12L20.59-6.12L20.59-6.12Q20.89-6.12 21.16-6.12L21.16-6.12L21.16-6.12Q21.43-6.12 21.70-6.11L21.70-6.11ZM22.71-8.11L22.71-9.12L24.68-9.12L24.68-5.57L24.68-5.57Q25.07-5.87 25.52-6.04L25.52-6.04L25.52-6.04Q25.98-6.22 26.47-6.22L26.47-6.22L26.47-6.22Q27.06-6.22 27.56-5.99L27.56-5.99L27.56-5.99Q28.06-5.77 28.42-5.37L28.42-5.37L28.42-5.37Q28.78-4.96 28.98-4.40L28.98-4.40L28.98-4.40Q29.18-3.83 29.18-3.15L29.18-3.15L29.18-3.15Q29.18-2.41 28.96-1.82L28.96-1.82L28.96-1.82Q28.73-1.22 28.34-0.80L28.34-0.80L28.34-0.80Q27.94-0.37 27.40-0.14L27.40-0.14L27.40-0.14Q26.85 0.09 26.20 0.09L26.20 0.09L26.20 0.09Q25.75 0.09 25.36-0.04L25.36-0.04L25.36-0.04Q24.96-0.17 24.68-0.38L24.68-0.38L24.68 0L23.59 0L23.59-8.11L22.71-8.11ZM26.18-5.26L26.18-5.26L26.18-5.26Q25.75-5.26 25.36-5.08L25.36-5.08L25.36-5.08Q24.97-4.90 24.68-4.61L24.68-4.61L24.68-1.34L24.68-1.34Q24.87-1.15 25.23-1.00L25.23-1.00L25.23-1.00Q25.59-0.85 26.14-0.85L26.14-0.85L26.14-0.85Q26.52-0.85 26.87-0.99L26.87-0.99L26.87-0.99Q27.21-1.13 27.47-1.42L27.47-1.42L27.47-1.42Q27.74-1.70 27.90-2.12L27.90-2.12L27.90-2.12Q28.05-2.54 28.05-3.11L28.05-3.11L28.05-3.11Q28.05-3.62 27.91-4.02L27.91-4.02L27.91-4.02Q27.76-4.42 27.50-4.69L27.50-4.69L27.50-4.69Q27.25-4.97 26.91-5.11L26.91-5.11L26.91-5.11Q26.57-5.26 26.18-5.26Z\" fill=\"#ffffff\"></path></g></g><style>text {\n" +
            "  font-size: 64px;\n" +
            "  font-family: Arial Black;\n" +
            "  dominant-baseline: central;\n" +
            "  text-anchor: middle;\n" +
            "}</style></svg>";
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

        List<Menu> appMenu = appMenuService.listWithSystemMenu(app.getAppId(), 0);
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

        String mainPage = app.getUrl().getPath();
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
