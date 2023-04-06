package curb.demo;

import curb.core.AccessLevel;
import curb.core.ApiResult;
import curb.core.CurbException;
import curb.core.DefaultPermissionResolver;
import curb.core.annotation.CurbMethod;
import curb.core.model.PermissionResult;
import curb.core.util.CurbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class DemoController {

    @ResponseBody
    @GetMapping("/hello")
    public ApiResult<String> hello(String name) {
        return new ApiResult<>(0, null, "hello, " + name);
    }

    @ResponseBody
    @GetMapping("/ajax/hello")
    @CurbMethod(resolver = DefaultPermissionResolver.class)
    public ApiResult<String> ajaxHello(String name) {
        return new ApiResult<>(0, null, "hello, " + name);
    }

    @GetMapping(value = "/page/hello")
    public String pageHello(ModelMap modelMap, String name) {
        modelMap.put("name", name);
        return "/page/hello";
    }

    @CurbMethod(resolver = DemoPermissionResolver.class)
    @ResponseBody
    @PostMapping("/ajax/hello_with_request_body")
    public ApiResult ajaxHelloWithRequestBody(@RequestBody DemoRequestBody requestBody) {
        return new ApiResult(0, null, requestBody);
    }

    /**
     * 一个复杂的多参数匹配的例子
     * 用户请求中的 dspIds，agentIds，均为逗号分割的ID值
     * 调用的接口中要对用户请求的这两个参数做权限限制，可能用户请求中的ID包含了没有权限的ID值，可通过PermissionResult 进行筛选，
     * 注解中设置  ignoreParamNames = {"dspIds", "agentIds"} 是为了跳过这两个参数的权限检查，在业务接口中进行过滤，
     * 否则如果用户请求中包含了任何不在用户权限内的值，权限拦截器都将拒绝用户请求
     *
     * @param requestBody
     * @return
     */
    @CurbMethod(resolver = DemoPermissionResolver.class, ignoreParamNames = {"dspIds", "agentIds"})
    @ResponseBody
    @PostMapping("/ajax/complex_case")
    public ApiResult ajaxComplexCase(@RequestBody DemoComplexRequestBody requestBody) {
        PermissionResult result = CurbUtil.getPermissionResult();
        // 取得用户请求的agentIds 中所有有权限的值
        Set<String> agentIds = result.intersectParamSet("agentIds");

        // 取得用户请求的dspIds 中所有有权限的值
        Set<String> dspIds = result.intersectParamSet("dspIds");
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("agentIds", agentIds);
        data.put("dspIds", dspIds);

        // 返回通过验证的所有权限串，仅用于进行权限判断结果的验证；
        data.put("matchedPermission", result.getAllowed());
        return new ApiResult(0, null, data);
    }

//    @ExceptionHandler(CurbException.class)
//    @ResponseBody
//    public ApiResult handleException(CurbException e) {
//        return e.toApiResult();
//    }
//
//    @ExceptionHandler(CurbException.class)
//    public ModelAndView handleExceptionForPage(CurbException e) {
//        return new ModelAndView("/page/error", "error", e.getMsg());
//    }
}
