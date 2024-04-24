package curb.core.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PermissionTest {

    @Test
    public void testIsPermited() throws Exception {
        {
            Permission p1 = Permission.build("/a/*,/b2,b1,b3/c1?e=e1&f=&g=g1&g=g2,g3&h=1,3,2&i");
            Permission pr = Permission.build("/a/aa/b2/c1?e=e1&f&g=g1,g2,g3&h=2&i=");
            assertFalse(pr.isAllowed(p1, true));
            assertTrue(p1.isAllowed(pr, true));
        }
        {
            Permission p1 = Permission.build("/a?x=1");
            Permission pr = Permission.build("/a/aa/?x=1");
            assertFalse(pr.isAllowed(p1, true));
            assertTrue(p1.isAllowed(pr, true));
        }
    }

    @Test
    public void testAllowed() {
        Permission req = Permission.build("/cms/page");
        assertTrue(Permission.build("/cms/page").isAllowed(req, true));
        assertTrue(Permission.build("/cms/page?templateId=119,124,6,7,8,9").isAllowed(req, false));
        assertFalse(Permission.build("/cms/page?templateId=119,124,6,7,8,9").isAllowed(req, true));
    }

}