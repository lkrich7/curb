package curb.server.util;

import curb.core.model.App;
import curb.core.model.Menu;
import curb.core.util.JsonUtil;
import curb.server.page.CurbPage;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import static org.junit.Assert.*;

public class CoreDataUtilTest {

    @Test
    public void testURI() {
        App app = new App();
        app.setName("test");
        app.setAppId(123);
        app.setUrl(URI.create("http://wwww.sogou.com/123"));
        String str = JsonUtil.toJSONString(app, true);
        System.err.println(str);
        App app2 = JsonUtil.parseObject(str, App.class);
        assertEquals(app.getUrl(), app2.getUrl());
    }
    @Test
    public void loadMenus() {
        List<Menu> menu = CoreDataUtil.loadMenus("curb/core-data/menus-footer.json");
        System.err.println(JsonUtil.toJSONString(menu, true));
    }

    @Test
    public void loadPages() {
        List<CurbPage> pages = CoreDataUtil.loadPages("curb/core-data/pages.json");
        for(CurbPage page : pages) {
            System.err.println(JsonUtil.toJSONString(JsonUtil.parseObject(page.getBody()), true));
        }
    }

    @Test
    public void test() {
//        String str = "{\"initApi\":\"get:/system/api/page/get?appId=${appId}&pageId=${pageId}\",\"type\":\"page\",\"title\":\"页面基本信息 - ${name} ({uri})\",\"body\":{\"controls\":[{\"name\":\"pageId\",\"type\":\"hidden\",\"required\":true},{\"name\":\"name\",\"label\":\"页面名称\",\"placeholder\":\"请输入页面名称\",\"type\":\"text\",\"required\":true},{\"name\":\"path\",\"label\":\"地址路径\",\"placeholder\":\"请输入地址路径\",\"type\":\"text\",\"required\":true},{\"selectFirst\":true,\"name\":\"type\",\"options\":[{\"label\":\"Amis\",\"value\":\"0\"},{\"label\":\"HTML\",\"value\":\"1\"}],\"label\":\"页面类型\",\"type\":\"select\",\"required\":true},{\"name\":\"accessLevel\",\"label\":\"访问控制\",\"type\":\"select\",\"selectFirst\":true,\"required\":true,\"options\":[{\"label\":\"仅允许有权限用户访问\",\"value\":\"2\"},{\"label\":\"允许所有登录用户访问\",\"value\":\"1\"},{\"label\":\"允许匿名访问\",\"value\":\"0\"}]},{\"visibleOn\":\"this.accessLevel == 2\",\"name\":\"sign\",\"label\":\"权限标识\",\"placeholder\":\"请输入权限标识, 缺省则使用访问页面的地址\",\"type\":\"text\"},{\"visibleOn\":\"this.accessLevel != 2\",\"name\":\"sign\",\"label\":\"权限标识\",\"type\":\"hidden\"}],\"initApi\":\"get:/system/api/page/get?appId=${appId}&pageId=${pageId}\",\"api\":{\"method\":\"post\",\"dataType\":\"form\",\"url\":\"/system/api/page/update?appId=${appId}\"},\"type\":\"form\",\"actions\":[{\"actionType\":\"link\",\"link\":\"/system/pages?appId=${appId}\",\"label\":\"返回\",\"type\":\"action\"},{\"level\":\"primary\",\"label\":\"保存\",\"type\":\"submit\"}]}}";
        try (InputStream is = CoreDataUtil.class.getClassLoader().getResourceAsStream("temp.json")) {
            String str = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            Map<String, Object> ret = JsonUtil.parseObject(str);
            System.err.println(JsonUtil.toJSONString(ret));
        } catch (IOException e) {
        }
    }
}