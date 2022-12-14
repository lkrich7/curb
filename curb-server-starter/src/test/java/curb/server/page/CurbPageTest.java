package curb.server.page;

import curb.server.page.CurbPage;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurbPageTest {

    @Test
    public void parsePath() {
        assertEquals("/asdasdas/asdasd/asd/d", CurbPage.parsePath("/asdasdas/asdasd/asd/d/?asdasd"));
        assertEquals("/asdasdas/asdasd/asd/d", CurbPage.parsePath("/asdasdas/asdasd/asd/d?asdasd"));
        assertEquals(null, CurbPage.parsePath("/asdasdas/asdasd/asd/d//?asdasd"));
        assertEquals(null, CurbPage.parsePath("/asdasdas//?asdasd"));
        assertEquals(null, CurbPage.parsePath("//?asdasd"));
        assertEquals("/", CurbPage.parsePath("/?/asdasd"));
        assertEquals("/", CurbPage.parsePath("/"));
    }
}