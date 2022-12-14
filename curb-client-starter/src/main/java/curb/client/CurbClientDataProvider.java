package curb.client;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import curb.core.ApiResult;
import curb.core.CurbDataProvider;
import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.AppDetail;
import curb.core.model.Group;
import curb.core.model.User;
import curb.core.model.UserAppPermissions;
import curb.core.model.UserPermission;
import curb.core.util.CurbUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Curb 数据服务接口客户端实现
 */
public class CurbClientDataProvider implements CurbDataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurbClientDataProvider.class);

    private final CurbApi curbApiHttpClient;

    private final Supplier<AppDetail> appDetailCache;

    private final LoadingCache<Integer, UserAppPermissions> userAppPermissionsCache;

    public CurbClientDataProvider(CurbApi curbApiHttpClient) {
        this.curbApiHttpClient = curbApiHttpClient;

        this.appDetailCache = Suppliers.memoizeWithExpiration(newAppDetailSupplier(), 60, TimeUnit.SECONDS);

        this.userAppPermissionsCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build(newUserAppPermissionsCacheLoader());
    }

    @Override
    public Group getGroup(HttpServletRequest request) {
        return appDetailCache.get().getGroup();
    }

    @Override
    public App getApp(HttpServletRequest request) {
        return appDetailCache.get().getApp();
    }

    @Override
    public User getUser(HttpServletRequest request) {
        String token = CurbUtil.getToken(request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return curbApiHttpClient.getUser(token).getData();
    }

    @Override
    public UserAppPermissions getUserAppPermissions(User user, App app, Group group) {
        if (user == null || user.getUserId() == null) {
            return UserAppPermissions.none();
        }
        try {
            return userAppPermissionsCache.get(user.getUserId());
        } catch (ExecutionException e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    private Supplier<AppDetail> newAppDetailSupplier() {
        return new Supplier<AppDetail>() {
            @Override
            public AppDetail get() {
                return curbApiHttpClient.getAppDetail().getData();
            }
        };
    }

    private CacheLoader<Integer, UserAppPermissions> newUserAppPermissionsCacheLoader() {
        return new CacheLoader<Integer, UserAppPermissions>() {
            @Override
            public UserAppPermissions load(Integer userId) {
                ApiResult<List<UserPermission>> result = curbApiHttpClient.listUserPermissions(userId);
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
