package curb.client;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.AppDetail;
import curb.core.model.User;
import curb.core.model.UserPermission;
import curb.core.util.ApiSignUtil;
import curb.core.util.JsonUtil;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Curb API HTTP客户端实现
 */
public class CurbApiHttpClient implements CurbApi {

    public static final String PARAM_NAME_USER_ID = "userId";

    private String server;
    private String appid;
    private String secret;
    private final CloseableHttpClient httpClient;

    public CurbApiHttpClient() {
        httpClient = initDefaultHttpClient();
    }

    public CurbApiHttpClient(String server, String appid, String secret) {
        this();
        this.server = server;
        this.appid = appid;
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public ApiResult<AppDetail> getAppDetail() {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        return invoke(API.GET_APP_DETAIL, paramsMap);
    }

    @Override
    public ApiResult<User> getUser(String token) {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("token", token);
        return invoke(API.GET_USER_INFO, paramsMap);
    }

    @Override
    public ApiResult<List<UserPermission>> listUserPermissions(Integer userId) {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put(PARAM_NAME_USER_ID, String.valueOf(userId));
        return invoke(API.LIST_USER_PERMISSION, paramsMap);
    }

    @SuppressWarnings("unchecked")
    private <T> ApiResult<T> invoke(final API api, Map<String, String> paramsMap) {
        HttpHost host = HttpHost.create(server);

        paramsMap.put("appId", appid);
        paramsMap.put("t", String.valueOf(System.currentTimeMillis()));
        String url = ApiSignUtil.signAndJoinToUrl(api.path, paramsMap, secret);
        HttpGet request = new HttpGet(url);

        try {
            return httpClient.execute(host, request, api);
        } catch (Exception e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        } finally {
            try {
                request.releaseConnection();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private CloseableHttpClient initDefaultHttpClient() {
        SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoTimeout(1000).build();

        ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).build();

        PoolingHttpClientConnectionManager defaultConnectionManager = new PoolingHttpClientConnectionManager();
        defaultConnectionManager.setDefaultSocketConfig(defaultSocketConfig);
        defaultConnectionManager.setDefaultConnectionConfig(defaultConnectionConfig);
        defaultConnectionManager.setDefaultMaxPerRoute(100);
        defaultConnectionManager.setMaxTotal(100);

        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(1000).setConnectTimeout(1000).setSocketTimeout(2000).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        return HttpClients.custom().setConnectionManager(defaultConnectionManager).setDefaultRequestConfig(defaultRequestConfig).disableCookieManagement().setKeepAliveStrategy(WrapDefaultConnectionKeepAliveStrategy.INSTANCE).setUserAgent("CurbClient-1.5.0").build();
    }

    enum API implements ResponseHandler<ApiResult> {
        GET_APP_DETAIL("/api/app/detail", new JsonUtil.GenericType<ApiResult<AppDetail>>() {
        }),
        GET_USER_INFO("/api/user/info", new JsonUtil.GenericType<ApiResult<User>>() {
        }),
        LIST_USER_PERMISSION("/api/permission/user/list", new JsonUtil.GenericType<ApiResult<List<UserPermission>>>() {
        }),
        ;
        private final String path;
        private final JsonUtil.GenericType<? extends ApiResult> genericType;

        API(String path, JsonUtil.GenericType<? extends ApiResult> genericType) {
            this.path = path;
            this.genericType = genericType;
        }

        @Override
        public ApiResult handleResponse(HttpResponse response) throws IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw ErrorEnum.SERVER_ERROR.toCurbException("网络请求异常:status code=" + status);
            }
            String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return JsonUtil.parseObject(json, genericType);
        }
    }
}
