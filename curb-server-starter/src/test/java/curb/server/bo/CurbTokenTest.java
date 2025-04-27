package curb.server.bo;

import curb.core.model.User;
import curb.core.util.Crypto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CurbTokenTest {

    @Test
    public void decrypt() {
        String secret = "1234567890abcdef";
        User user = new User();
        user.setUserId(1);
        user.setName("ROOT");
        user.setUsername("root");
        CurbToken t = new CurbToken(user.getUsername(), user.toString());
        String encrypted = t.encrypt(secret, Crypto.AES);
        System.out.println(encrypted);
        assertNotNull(encrypted);
        CurbToken t2 = CurbToken.decrypt(encrypted, secret, Crypto.AES);

        assertEquals(t, t2);

    }

    @Test
    public void encrypt() {
    }
}