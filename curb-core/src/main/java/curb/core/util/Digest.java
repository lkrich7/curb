package curb.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Digest {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    ;

    private final String algorithm;

    Digest(String algorithm) {
        this.algorithm = algorithm;
    }

    public String digestAsHex(byte[] source) {
        return digestAsHex(source, false);
    }

    public String digestAsHex(byte[] source, boolean upperCase) {
        return HexCodec.encode(digest(source), upperCase);
    }

    public String digestAsHex(String source) {
        return digestAsHex(source.getBytes(StandardCharsets.UTF_8), false);
    }

    public String digestAsHex(String source, boolean upperCase) {
        return digestAsHex(source.getBytes(StandardCharsets.UTF_8), upperCase);
    }

    public byte[] digest(byte[] source) {
        return getInstance().digest(source);
    }

    public MessageDigest getInstance() {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", e);
        }
    }

}
