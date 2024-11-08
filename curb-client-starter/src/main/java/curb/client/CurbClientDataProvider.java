package curb.client;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import curb.client.configuration.CurbClientProperties;
import curb.core.ApiResult;
import curb.core.CurbDataProvider;
import curb.core.CurbRequestContext;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.AppDetail;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.model.UserPermission;
import curb.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Curb 数据服务接口客户端实现
 */
public class CurbClientDataProvider implements CurbDataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbClientDataProvider.class);

    private final CurbApi curbApiClient;

    private final CurbClientProperties properties;

    private final Supplier<AppDetail> appDetailCache;

    private final LoadingCache<Integer, UserAppPermissions> userAppPermissionsCache;

    public CurbClientDataProvider(CurbApi curbApiClient, CurbClientProperties properties) {
        this.curbApiClient = curbApiClient;
        this.properties = properties;

        this.appDetailCache = Suppliers.memoizeWithExpiration(newAppDetailSupplier(), 60, TimeUnit.SECONDS);

        this.userAppPermissionsCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build(newUserAppPermissionsCacheLoader());
    }

    @Override
    public App getApp(String url) {
        return appDetailCache.get().getApp();
    }

    @Override
    public Group getGroup(CurbRequestContext context) {
        return appDetailCache.get().getGroup();
    }

    @Override
    public User getUser(String encryptedToken, CurbRequestContext context) {
        if (StringUtil.isBlank(encryptedToken)) {
            return null;
        }
        return curbApiClient.getUser(encryptedToken).getData();
    }

    @Override
    public UserAppPermissions getUserAppPermissions(CurbRequestContext context) {
        User user = context.getUser();
        if (user == null || user.getUserId() == null) {
            return UserAppPermissions.none();
        }
        try {
            return userAppPermissionsCache.get(user.getUserId());
        } catch (ExecutionException e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    @Override
    public void recordRequest(CurbRequestContext context) {
        // TODO: 待实现
    }

    private Supplier<AppDetail> newAppDetailSupplier() {
        return () -> curbApiClient.getAppDetail().getData();
    }

    private CacheLoader<Integer, UserAppPermissions> newUserAppPermissionsCacheLoader() {
        return new CacheLoader<Integer, UserAppPermissions>() {
            @Override
            public UserAppPermissions load(Integer userId) {
                ApiResult<List<UserPermission>> result = curbApiClient.listUserPermissions(userId);
                if (result.getStatus() == 0 && result.getData() != null) {
                    return UserAppPermissions.build(result.getData());
                } else {
                    LOGGER.warn("error on load user permission: userId={}, result={}", userId, result);
                    return UserAppPermissions.none();
                }
            }
        };
    }

}
