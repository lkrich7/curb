package curb.server.service;

import curb.core.util.JsonUtil;
import curb.server.page.CurbPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    private PageService pageService;

    @Test
    public void getPage() {
        CurbPage page = pageService.getPage(1, "/system/menus");
        System.err.println(JsonUtil.toJSONString(JsonUtil.parseObject(page.getBody()), true));
    }
}