package curb.core.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HexCodecTest {

    @Test
    public void testEncodeLowerCase() {
        byte[] input = {0x1a, (byte) 0xf2, 0x3c};
        String expectedOutput = "1af23c";
        assertEquals(expectedOutput, HexCodec.encode(input));
    }

    @Test
    public void testEncodeUpperCase() {
        byte[] input = {0x1a, (byte) 0xf2, 0x3c};
        String expectedOutput = "1AF23C";
        assertEquals(expectedOutput, HexCodec.encode(input, true));
    }

    @Test
    public void testDecodeValidHexString() {
        String input = "1af23c";
        byte[] expectedOutput = {0x1a, (byte) 0xf2, 0x3c};
        assertArrayEquals(expectedOutput, HexCodec.decode(input));
    }

    @Test
    public void testDecodeOddLengthHexString() {
        String input = "1af23"; // Odd length hex string
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            HexCodec.decode(input);
        });
        assertEquals("Invalid hex length: 5", exception.getMessage());
    }


    @Test
    public void testDecodeInvalidHexString() {
        String input = "1af23cg0"; // Invalid hex character 'g'
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            HexCodec.decode(input);
        });
        assertEquals("Invalid hex char at position 6: 1af23cg0", exception.getMessage());
    }

    private static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return expectedType.cast(actual);
            } else {
                fail("Expected " + expectedType.getName() + " but got " + actual.getClass().getName());
            }
        }
        fail("Expected " + expectedType.getName() + " to be thrown but nothing was thrown.");
        return null; // unreachable
    }
}
