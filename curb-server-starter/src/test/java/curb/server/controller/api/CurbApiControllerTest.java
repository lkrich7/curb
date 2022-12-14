package curb.server.controller.api;

import curb.server.configuration.CurbServerAutoConfiguration;
import curb.server.service.AppService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = CurbServerAutoConfiguration.class)
public class CurbApiControllerTest {

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private AppService appService;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    @Test
    public void test() throws Exception {
        mockMvc.perform(get("/"));
    }

}