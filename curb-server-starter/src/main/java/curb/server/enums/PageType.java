package curb.server.enums;

import curb.core.CodedEnum;
import curb.core.ErrorEnum;
import curb.core.util.JsonUtil;
import org.springframework.web.util.HtmlUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 页面类型
 */
public enum PageType implements CodedEnum<PageType> {

    AMIS(0, "amis") {
        @Override
        public String checkOrInitBody(String body, String name) {
            if (body == null || body.isEmpty()) {
                return initBody(name);
            }
            try {
                Object schema = JsonUtil.parseObject(body);
                return JsonUtil.toJSONString(schema, true);
            } catch (Exception e) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("页面结构格式错误", e);
            }
        }

        @Override
        public String initBody(String name) {
            Map<String, Object> schema = new LinkedHashMap<>();
            schema.put("type", "page");
            schema.put("title", name);
            return JsonUtil.toJSONString(schema, true);
        }
    },

    HTML(1, "HTML") {
        @Override
        public String checkOrInitBody(String body, String name) {
            if (body == null || body.isEmpty()) {
                return initBody(name);
            }
            return body.trim();
        }

        @Override
        public String initBody(String name) {
            return String.format("<H1>%s</H1>", HtmlUtils.htmlEscape(name));
        }
    },
    ;


    private final int code;
    private final String name;

    PageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return stringify();
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 对页面结构格式进行检查修正
     *
     * @param body 页面结构文本
     * @param name 页面名称 (用于辅助初始化默认页面结构）
     * @return 检查之后的页面结构文本，如果body为空 则返回初始化默认页面结构
     */
    public abstract String checkOrInitBody(String body, String name);

    /**
     * 初始化默认页面结构
     *
     * @param name 页面名称
     * @return 初始化的页面结构
     */
    public abstract String initBody(String name);

    public static PageType valueOfCode(Integer code) {
        return CodedEnum.valueOfCode(PageType.class, code, null);
    }
    public static PageType valueOfCode(Integer code, PageType defaultValue) {
        return CodedEnum.valueOfCode(PageType.class, code, defaultValue);
    }

}
