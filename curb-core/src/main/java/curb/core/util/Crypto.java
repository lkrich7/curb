package curb.core.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public enum Crypto {
    AES("AES", "ECB", "PKCS5Padding"),
    ;

    private final String algorithm;
    private final String mode;
    private final String padding;

    Crypto(String algorithm, String mode, String padding) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.padding = padding;
    }

    public String encrypt(String raw, String secret) {
        byte[] input = raw.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = buildCipher();
        Key key = initKey(secret);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] output = cipher.doFinal(input);
            return Base64Codec.URL_SAFE.encodeToString(output);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String encrypted, String secret) {
        byte[] input = Base64Codec.URL_SAFE.decode(encrypted);
        Cipher cipher = buildCipher();
        Key key = initKey(secret);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] output = cipher.doFinal(input);
            return new String(output, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher buildCipher() {
        try {
            return Cipher.getInstance(String.format("%s/%s/%s", algorithm, mode, padding));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Key initKey(String secret) {
        if (null == secret || secret.isEmpty()) {
            throw new IllegalArgumentException("secret should not be empty");
        }
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(secret.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            return new SecretKeySpec(enCodeFormat, algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
