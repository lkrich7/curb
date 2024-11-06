package curb.core.mvc.interceptor;

import curb.core.AccessLevel;
import curb.core.AccessState;
import curb.core.CurbAccessConfig;
import curb.core.CurbDataProvider;
import curb.core.CurbMethodAccessConfig;
import curb.core.CurbRequestContext;
import curb.core.ErrorEnum;
import curb.core.PermissionResolver;
import curb.core.annotation.CurbMethod;
import curb.core.configuration.CurbProperties;
import curb.core.model.App;
import curb.core.model.Group;
import curb.core.model.Permission;
import curb.core.model.PermissionResult;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.model.UserState;
import curb.core.mvc.proxy.CurbReverseProxyHandlerMapping;
import curb.core.util.CurbUtil;
import curb.core.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Curb核心拦截器
 */
public class CurbInterceptor implements HandlerInterceptor, ApplicationContextAware {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CurbInterceptor.class);

    private static final CurbAccessConfig DEFAULT_REQUEST_CONFIG = new CurbMethodAccessConfig();

    /**
     * 数据提供者
     */
    private final CurbDataProvider dataProvider;

    /**
     * 默认的权限解析器
     */
    private final PermissionResolver defaultPermissionResolver;

    private final CurbProperties curbProperties;

    private ApplicationContext applicationContext;

    public CurbInterceptor(CurbDataProvider dataProvider, PermissionResolver defaultPermissionResolver, CurbProperties properties) {
        this.dataProvider = dataProvider;
        this.defaultPermissionResolver = defaultPermissionResolver;
        this.curbProperties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String toString() {
        return String.format("%s(%s, defaultResolver=%s, includeDispatcherTypes=%s, excludeStaticResource=%s)",
                getClass(),
                curbProperties.getTestMode(),
                defaultPermissionResolver,
                curbProperties.getIncludeDispatcherTypes(),
                curbProperties.isExcludeStaticResource());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (curbProperties.excludeDispatcherType(request.getDispatcherType())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipped for DispatcherType({}): {} {}", request.getDispatcherType(), request.getMethod(), CurbUtil.getUrl(request));
            }
            return true;
        }
        if (curbProperties.isExcludeStaticResource() && isStaticResourceRequest(request, handler)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipped for static resource request: {} {}", request.getMethod(), CurbUtil.getUrl(request));
            }
            return true;
        }

        CurbRequestContext context = CurbUtil.createContext(request);
        context.setTestMode(curbProperties.inTestMode());

        App app = getApp(context);
        context.setApp(app);

        Group group = getGroup(context);
        context.setGroup(group);

        User user = getUser(request, context);
        context.setUser(user);

        CurbAccessConfig config = resolveAccessConfig(request, handler);
        if (config == null) {
            config = DEFAULT_REQUEST_CONFIG;
        }
        context.setAccessConfig(config);
        AccessLevel accessLevel = config.getLevel();

        if (accessLevel == AccessLevel.ANONYMOUS) {
            // 允许匿名访问
            context.setAccessState(AccessState.ANONYMOUS);
            return true;
        }

        if (isAuthenticated(context)) {
            onAuthenticated(context, request, response, handler);
        } else {
            // 用户账号状态不正常
            context.setAccessState(context.userState() == UserState.BLOCKED ? AccessState.USER_BLOCKED : AccessState.USER_NOT_EXISTED);
            onUnauthenticated(context, request, response, handler);
            return false;
        }

        if (accessLevel == AccessLevel.LOGIN) {
            // 允许登录用户访问，直接返回
            context.setAccessState(AccessState.LOGIN);
            return true;
        }

        // 权限验证
        UserAppPermissions userAppPermissions = getUserAppPermissions(context);
        Permission requestPermission = parseRequestPermission(request, config, handler);
        PermissionResult permissionResult = userAppPermissions.check(requestPermission, config);
        context.setPermissionResult(permissionResult);

        if (isAuthorized(context)) {
            context.setAccessState(AccessState.AUTHORIZED);
            onAuthorized(context, request, response, handler);
            return true;
        } else {
            context.setAccessState(AccessState.UNAUTHORIZED);
            onUnauthorized(context, request, response, handler);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurbRequestContext context = CurbUtil.getContext(request);
        if (context == null) {
            return;
        }
        context.setEndTime(System.currentTimeMillis());
        context.setHttpStatus(response.getStatus());
        recordRequest(context, request, response, handler, ex);
    }

    /**
     * 判断当前请求是否是静态资源请求
     */
    protected boolean isStaticResourceRequest(HttpServletRequest request, Object handler) {
        return handler instanceof ResourceHttpRequestHandler;
    }

    /**
     * 记录请求日志
     */
    protected void recordRequest(CurbRequestContext context,
                                 HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        AccessLevel level = context.getAccessConfig().getLevel();
        String username = Optional.ofNullable(context.getUser()).map(User::getUsername).orElse(null);
        LOGGER.info("{} T({}) G({}) A({}) {} {}ms: {} {} {}({})@{}",
                context.getAccessState(), context.isTestMode(),
                context.getGroup().getGroupId(), context.getApp().getAppId(),
                level, context.costMs(),
                context.getMethod(), context.getUrl(), username, context.userState(), context.getIp());
        dataProvider.recordRequest(context);
    }

    /**
     * 获取应用基本信息
     */
    protected App getApp(CurbRequestContext context) {
        return dataProvider.getApp(context.getUrl());
    }

    /**
     * 获取项目组信息
     */
    protected Group getGroup(CurbRequestContext request) {
        return dataProvider.getGroup(request);
    }

    /**
     * 根据请求中的登录态获取用户信息
     */
    protected User getUser(HttpServletRequest request, CurbRequestContext context) {
        // 测试模式下取测试用户
        User user = curbProperties.testUser();
        if (user != null) {
            return user;
        }
        String encryptedToken = ServletUtil.getCookie(request, curbProperties.getTokenName());
        return dataProvider.getUser(encryptedToken, context);
    }

    /**
     * 获取用户权限列表
     */
    protected UserAppPermissions getUserAppPermissions(CurbRequestContext context) {
        // 测试模式下取测试用户权限
        UserAppPermissions ret = curbProperties.testUserAppPermissions();
        if (ret != null) {
            return ret;
        }
        return dataProvider.getUserAppPermissions(context);
    }

    /**
     * 获取当前请求的配置数据
     */
    protected CurbAccessConfig resolveAccessConfig(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            CurbMethod annotation = handlerMethod.getMethodAnnotation(CurbMethod.class);
            if (annotation != null) {
                return new CurbMethodAccessConfig(annotation);
            }
        } else if (handler instanceof CurbReverseProxyHandlerMapping.RouteHandler) {
            CurbReverseProxyHandlerMapping.RouteHandler proxyHandler = (CurbReverseProxyHandlerMapping.RouteHandler) handler;
            return proxyHandler.resolveAccessConfig(request);
        }
        return null;
    }

    /**
     * 解析请求权限对象
     */
    protected Permission parseRequestPermission(HttpServletRequest request, CurbAccessConfig config, Object handler) {
        if (config.getSign() != null && !config.getSign().isEmpty()) {
            return Permission.build(config.getSign());
        }
        Class<? extends PermissionResolver> resolverClass = config.getResolverClass();
        PermissionResolver resolver;
        if (resolverClass == null
                || resolverClass == PermissionResolver.class
                || resolverClass == defaultPermissionResolver.getClass()) {
            resolver = defaultPermissionResolver;
        } else {
            resolver = applicationContext.getBean(resolverClass);
        }
        try {
            return resolver.resolve(request, config, handler);
        } catch (Exception e) {
            LOGGER.warn("failed to parse permission : {}, use null", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断当前用户是否身份认证通过
     */
    protected boolean isAuthenticated(CurbRequestContext context) {
        return context.isAuthenticated();
    }

    /**
     * 身份认证通过时的回调方法
     */
    protected void onAuthenticated(CurbRequestContext context, HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 身份验证通过默认不做处理
    }

    /**
     * 登录态验证不通过时的处理函数
     */
    protected void onUnauthenticated(CurbRequestContext context,
                                     HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (context.userState() == UserState.NOT_EXISTED) {
            throw ErrorEnum.NEED_LOGIN.toCurbException();
        }
        throw ErrorEnum.USER_BLOCKED.toCurbException();
    }

    /**
     * 判断是否通过权限验证
     */
    protected boolean isAuthorized(CurbRequestContext context) {
        return context.isAuthorized();
    }

    /**
     * 权限验证通过时处理函数
     */
    protected void onAuthorized(CurbRequestContext context,
                                HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 权限检查通过默认不做处理
    }

    /**
     * 处理无作权限情况
     */
    protected void onUnauthorized(CurbRequestContext context,
                                  HttpServletRequest request, HttpServletResponse response, Object handler) {
        throw ErrorEnum.FORBIDDEN.toCurbException();
    }

}
