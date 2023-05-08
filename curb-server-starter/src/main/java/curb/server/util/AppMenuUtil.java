package curb.server.util;

import curb.core.model.Menu;
import curb.core.util.JsonUtil;
import curb.core.util.StringUtil;

import java.util.Collections;
import java.util.List;

/**
 * 应用菜单数据工具类
 */
public final class AppMenuUtil {

    private AppMenuUtil() {
    }

    public static List<Menu> parse(String str) {
        if (StringUtil.isBlank(str)) {
            return Collections.emptyList();
        }
        List<Menu> ret = JsonUtil.parseArray(str, Menu.class);
        if (ret == null || ret.isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }

    public static String serialize(List<Menu> menu) {
        if (menu == null) {
            return "[]";
        }
        return JsonUtil.toJSONString(menu, true);
    }
}
