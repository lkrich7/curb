package curb.core.mvc.interceptor;

import curb.core.AccessLevel;
import curb.core.CurbAccessConfig;
import curb.core.CurbDataProvider;
import curb.core.CurbMethodAccessConfig;
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
import curb.core.util.CurbUtil;
import curb.core.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EnumSet;
import java.util.Set;

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

    private static String buildLogMessage(HttpServletRequest request, AccessLevel accessLevel,
                                          Group group, App app, User user, UserState userState, boolean inTestMode) {
        return String.format("T(%s) G(%s) A(%s) %s (%s %s) %s(%s)@%s",
                inTestMode,
                group.getGroupId(),
                app.getAppId(),
                accessLevel,
                request.getMethod(),
                CurbUtil.getUrl(request),
                user == null ? null : user.getUsername(),
                userState,
                ServletUtil.getIp(request));
    }

    /**
     * 参与执行检查的请求的DispatcherType集合
     */
    private Set<DispatcherType> includeDispatcherTypes() {
        Set<DispatcherType> includeDispatcherTypes = curbProperties.getIncludeDispatcherTypes();
        if (includeDispatcherTypes != null && !includeDispatcherTypes.isEmpty()) {
            return EnumSet.copyOf(includeDispatcherTypes);
        }
        return EnumSet.of(DispatcherType.REQUEST);
    }

    /**
     * 是否排除对静态资源请求的检查
     */
    private boolean excludeStaticResource() {
        return curbProperties.isExcludeStaticResource();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String toString() {
        return String.format("%s(testMode=%s, defaultResolver=%s, includeDispatcherTypes=%s, excludeStaticResource=%s)",
                getClass(), curbProperties.getTestMode(), defaultPermissionResolver, includeDispatcherTypes(), excludeStaticResource());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!includeDispatcherTypes().contains(request.getDispatcherType())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Passed for DispatcherType({}): {} {}?{}, ", request.getDispatcherType(),
                        request.getMethod(), request.getRequestURI(), request.getQueryString());
            }
            return true;
        }
        if (excludeStaticResource() && isStaticResourceRequest(request, handler)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Passed for static resource request: {} {}?{}",
                        request.getMethod(), request.getRequestURI(), request.getQueryString());
            }
            return true;
        }

        App app = getApp(request);
        CurbUtil.setApp(request, app);

        Group group = getGroup(request);
        CurbUtil.setGroup(request, group);

        User user = getUser(request);
        CurbUtil.setUser(request, user);

        UserState userState = checkUserState(user);

        CurbAccessConfig config = resolveAccessConfig(request, handler);
        if (config == null) {
            config = DEFAULT_REQUEST_CONFIG;
        }
        AccessLevel accessLevel = config.getLevel();

        String msg = buildLogMessage(request, accessLevel, group, app, user, userState, curbProperties.inTestMode());

        if (accessLevel == AccessLevel.ANONYMOUS) {
            // 允许匿名访问，直接返回
            LOGGER.info("Passed(Anonymous): {}", msg);
            return true;
        }

        if (isAuthenticated(user, userState)) {
            onAuthenticated(user, app, group, request, response, handler);
        } else {
            // 用户登录不正常
            LOGGER.info("Denied(Unauthenticated): {}", msg);
            return onUnauthenticated(user, userState, app, group, request, response, handler);
        }

        if (accessLevel == AccessLevel.LOGIN) {
            // 允许登录用户访问，直接返回
            LOGGER.info("Passed(Login): {}", msg);
            return true;
        }

        // 权限验证
        Permission requestPermission = parseRequestPermission(request, config, handler);
        UserAppPermissions userAppPermissions = getUserAppPermissions(user, app, group, request);
        PermissionResult permissionResult = userAppPermissions.check(requestPermission, config);
        CurbUtil.setPermissionResult(request, permissionResult);

        if (isAuthorized(permissionResult)) {
            LOGGER.info("Passed(Permission): {}", msg);
            return onAuthorized(user, app, group, request, response, handler);
        } else {
            LOGGER.info("Denied(Permission): {}", msg);
            return onUnauthorized(user, app, group, request, response, handler);
        }
    }

    /**
     * 判断当前请求是否是静态资源请求
     *
     * @param request
     * @param handler
     * @return
     */
    protected boolean isStaticResourceRequest(HttpServletRequest request, Object handler) {
        return handler instanceof ResourceHttpRequestHandler;
    }

    /**
     * 获取当前请求的配置数据
     *
     * @param request
     * @param handler
     * @return
     */
    protected CurbAccessConfig resolveAccessConfig(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            CurbMethod annotation = handlerMethod.getMethodAnnotation(CurbMethod.class);
            if (annotation != null) {
                return new CurbMethodAccessConfig(annotation);
            }
        }
        return DEFAULT_REQUEST_CONFIG;
    }

    /**
     * 解析请求权限对象
     *
     * @param request
     * @param config
     * @param handler
     * @return
     */
    protected Permission parseRequestPermission(HttpServletRequest request, CurbAccessConfig config, Object handler) {
        if (config.getSign() != null && !config.getSign().isEmpty()) {
            return Permission.build(config.getSign());
        }
        Class<? extends PermissionResolver> resolverClass = config.getResolverClass();
        PermissionResolver resolver;
        if (resolverClass == null || resolverClass == defaultPermissionResolver.getClass()) {
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
     * 获取项目组信息
     *
     * @param request
     * @return
     */
    protected Group getGroup(HttpServletRequest request) {
        return dataProvider.getGroup(request);
    }

    /**
     * 获取应用基本信息
     *
     * @param request
     * @return
     */
    protected App getApp(HttpServletRequest request) {
        return dataProvider.getApp(request);
    }

    /**
     * 根据请求中的登录态获取用户信息
     *
     * @param request
     * @return
     */
    protected User getUser(HttpServletRequest request) {
        // 测试模式下取测试用户
        User user = curbProperties.testUser();
        if (user != null) {
            return user;
        }
        return dataProvider.getUser(request);
    }

    /**
     * 获取用户权限列表
     *
     * @param user
     * @return
     */
    protected UserAppPermissions getUserAppPermissions(User user, App app, Group group, HttpServletRequest request) {
        // 测试模式下取测试用户权限
        UserAppPermissions ret = curbProperties.testUserAppPermissions();
        if (ret != null) {
            return ret;
        }
        return dataProvider.getUserAppPermissions(user, app, group);
    }

    /**
     * 检查用户信息是否正常登录
     *
     * @param user
     * @return
     */
    protected UserState checkUserState(User user) {
        return UserState.check(user);
    }

    /**
     * 判断当前用户是否登录态验证通过
     *
     * @param user
     * @param userState
     * @return
     */
    protected boolean isAuthenticated(User user, UserState userState) {
        return user != null && userState.isOk();
    }

    /**
     * 登录态验证通过
     *
     * @param user     当前用户
     * @param app
     * @param group
     * @param request
     * @param response
     * @param handler
     */
    protected void onAuthenticated(User user, App app, Group group, HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 身份验证通过默认不做处理
    }

    /**
     * 登录态验证不通过时的处理函数
     *
     * @param user     当前用户
     * @param state    当前用户状态
     * @param request
     * @param response
     * @param handler
     * @return
     */
    protected boolean onUnauthenticated(User user, UserState state, App app, Group group,
                                        HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (user == null) {
            throw ErrorEnum.NEED_LOGIN.toCurbException();
        }
        if (state == UserState.NOT_EXISTED) {
            throw ErrorEnum.NEED_LOGIN.toCurbException();
        }
        throw ErrorEnum.USER_BLOCKED.toCurbException();
    }

    /**
     * 判断是否通过权限验证
     *
     * @param permissionResult
     * @return
     */
    protected boolean isAuthorized(PermissionResult permissionResult) {
        return permissionResult.isAuthorized();
    }

    /**
     * 权限验证通过时处理函数
     *
     * @param user     当前用户
     * @param app
     * @param group
     * @param request
     * @param response
     * @param handler
     * @return
     */
    protected boolean onAuthorized(User user, App app, Group group, HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    /**
     * 处理无作权限情况
     *
     * @param user     当前用户
     * @param app
     * @param group
     * @param request
     * @param response
     * @param handler
     * @return
     */
    protected boolean onUnauthorized(User user, App app, Group group,
                                     HttpServletRequest request, HttpServletResponse response, Object handler) {
        throw ErrorEnum.FORBIDDEN.toCurbException();
    }

}
