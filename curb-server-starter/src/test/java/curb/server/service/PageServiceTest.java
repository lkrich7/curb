package curb.server.service;

import curb.core.util.JsonUtil;
import curb.server.page.CurbPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PageServiceTest {

    @Autowired
    private PageService pageService;

    @Test
    public void getPage() {
        CurbPage page = pageService.getPage(1, "/system/users");
        System.err.println(JsonUtil.toJSONString(JsonUtil.parseObject(page.getBody()), true));
    }
}