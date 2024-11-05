package curb.client;

import curb.core.ApiResult;
import curb.core.ErrorEnum;
import curb.core.model.AppDetail;
import curb.core.model.User;
import curb.core.model.UserPermission;
import curb.core.util.ApiSignUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

/**
 * 基于RestTemplate的 Curb API 客户端实现
 */
public class CurbApiRestClient implements CurbApi {

    private final RestTemplate restTemplate;

    private final String server;
    private final String appid;
    private final String secret;

    public static final String PARAM_NAME_USER_ID = "userId";

    static final ApiDef<AppDetail> GET_APP_DETAIL = new ApiDef<>(GET, "/api/app/detail", new ParameterizedTypeReference<ApiResult<AppDetail>>() {
    });
    static final ApiDef<User> GET_USER_INFO = new ApiDef<>(GET, "/api/user/info", new ParameterizedTypeReference<ApiResult<User>>() {
    });
    static final ApiDef<List<UserPermission>> LIST_USER_PERMISSION = new ApiDef<>(GET, "/api/permission/user/list", new ParameterizedTypeReference<ApiResult<List<UserPermission>>>() {
    });

    public CurbApiRestClient(RestTemplate restTemplate, String server, String appid, String secret) {
        this.restTemplate = restTemplate;
        this.server = server;
        this.appid = appid;
        this.secret = secret;
    }

    @Override
    public ApiResult<AppDetail> getAppDetail() {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        return invoke(GET_APP_DETAIL, paramsMap);
    }

    @Override
    public ApiResult<User> getUser(String token) {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("token", token);
        return invoke(GET_USER_INFO, paramsMap);
    }

    @Override
    public ApiResult<List<UserPermission>> listUserPermissions(Integer userId) {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put(PARAM_NAME_USER_ID, String.valueOf(userId));
        return invoke(LIST_USER_PERMISSION, paramsMap);
    }

    protected <T> ApiResult<T> invoke(RestApi<T> api, Map<String, String> paramsMap) {
        paramsMap.put("appId", appid);
        paramsMap.put("t", String.valueOf(System.currentTimeMillis()));
        ApiSignUtil.signAndPutToMap(secret, paramsMap);

        RequestEntity<?> request = api.buildRequestEntity(server, paramsMap);
        try {
            ResponseEntity<ApiResult<T>> response = restTemplate.exchange(request, api.getGenericType());
            return response.getBody();
        } catch (Exception e) {
            throw ErrorEnum.SERVER_ERROR.toCurbException(e);
        }
    }

    public interface RestApi<T> {
        RequestEntity<?> buildRequestEntity(String server, Map<String, String> paramsMap);

        ParameterizedTypeReference<ApiResult<T>> getGenericType();
    }

    static class ApiDef<T> implements RestApi<T> {

        private final HttpMethod method;
        private final String path;
        private final ParameterizedTypeReference<ApiResult<T>> genericType;

        ApiDef(HttpMethod method, String path, ParameterizedTypeReference<ApiResult<T>> genericType) {
            this.method = method;
            this.path = path;
            this.genericType = genericType;
        }

        @Override
        public RequestEntity<?> buildRequestEntity(String server, Map<String, String> paramsMap) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            URI url = buildUrl(server, paramsMap);
            Object body = method == HttpMethod.GET ? null : paramsMap;
            return new RequestEntity<>(body, headers, method, url);
        }

        @Override
        public ParameterizedTypeReference<ApiResult<T>> getGenericType() {
            return genericType;
        }

        private URI buildUrl(String server, Map<String, String> paramsMap) {
            if (method == HttpMethod.GET && !paramsMap.isEmpty()) {
                String paramStr = ApiSignUtil.joinParams(paramsMap);
                return URI.create(server + path + "?" + paramStr);
            } else {
                return URI.create(server + path);
            }
        }
    }

}
