package curb.client;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.AppDetail;
import curb.core.model.User;
import curb.core.model.UserPermission;
import curb.core.util.ApiSignUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于RestTemplate的 Curb API 客户端实现
 */
public class CurbApiRestClient implements CurbApi {

    public static final String PARAM_NAME_USER_ID = "userId";

    private final RestTemplate restTemplate;

    private final String server;
    private final String appid;
    private final String secret;

    public CurbApiRestClient(RestTemplate restTemplate, String server, String appid, String secret) {
        this.restTemplate = restTemplate;
        this.server = server;
        this.appid = appid;
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
        paramsMap.put("appId", appid);
        paramsMap.put("t", String.valueOf(System.currentTimeMillis()));
        String url = ApiSignUtil.signAndJoinToUrl(server + api.path, paramsMap, secret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity request = new HttpEntity(headers);

        try {
            ResponseEntity<? extends ApiResult> response = restTemplate.exchange(url, HttpMethod.GET, request, api.genericType);
            ApiResult<T> ret = response.getBody();
            return ret;
        } catch (Exception e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    enum API {
        GET_APP_DETAIL("/api/app/detail", new ParameterizedTypeReference<ApiResult<AppDetail>>() {
        }),
        GET_USER_INFO("/api/user/info", new ParameterizedTypeReference<ApiResult<User>>() {
        }),
        LIST_USER_PERMISSION("/api/permission/user/list", new ParameterizedTypeReference<ApiResult<List<UserPermission>>>() {
        }),
        ;
        private final String path;
        private final ParameterizedTypeReference<? extends ApiResult> genericType;

        API(String path, ParameterizedTypeReference<? extends ApiResult> genericType) {
            this.path = path;
            this.genericType = genericType;
        }

    }
}
