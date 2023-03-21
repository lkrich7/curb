package curb.server.dao;

import curb.server.po.AppPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppDAOTest {

    @Autowired
    private AppDAO target;

    @Test
    public void get() {
        Integer appId = 1;
        AppPO actual = target.get(appId);
        assertNotNull(actual);
        assertEquals(appId, actual.getAppId());
    }

    @Test
    public void getByDomain() {
    }

    @Test
    public void listByGroupId() {
    }

    @Test
    public void insert() {
    }

    @Test
    public void update() {
    }

    @Test
    public void updateState() {
    }

    @Test
    public void delete() {
    }
}