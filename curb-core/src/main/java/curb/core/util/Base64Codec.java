package curb.core.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

public enum Base64Codec {
    RAW(Base64.getEncoder(), Base64.getDecoder()),
    URL_SAFE(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder()),
    MIME(Base64.getMimeEncoder(), Base64.getMimeDecoder());

    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    Base64Codec(Base64.Encoder encoder, Base64.Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public String encodeToString(byte[] src) {
        return encoder.encodeToString(src);
    }

    public byte[] decode(String src) {
        return decoder.decode(src);
    }

    public byte[] encode(byte[] src) {
        return encoder.encode(src);
    }

    public byte[] decode(byte[] src) {
        return decoder.decode(src);
    }

    public int encode(byte[] src, byte[] dst) {
        return encoder.encode(src, dst);
    }

    public int decode(byte[] src, byte[] dst) {
        return decoder.decode(src, dst);
    }

    public ByteBuffer encode(ByteBuffer buffer) {
        return encoder.encode(buffer);
    }

    public ByteBuffer decode(ByteBuffer src) {
        return decoder.decode(src);
    }

    public OutputStream wrap(OutputStream os) {
        return encoder.wrap(os);
    }

    public InputStream wrap(InputStream is) {
        return decoder.wrap(is);
    }

}
