package util;

import kernel.Kernel;

public class BitHelper {
    @SJC.Inline
    public static boolean getFlag(int value, int n) {
        int flag = ((value >> n) & 1);
        return flag == 1;
    }

    @SJC.Inline
    public static int setFlag(int value, int n, boolean flag) {
        return ((value & ~(1 << n)) | ((flag ? 1 : 0) << n));
    }

    @SJC.Inline
    public static int setRange(int value, int start, int length, int newValue) {
        int highBits = (1 << length) - 1;
        int loadMask = highBits << start;
        int storeMask = (newValue & highBits) << start;
        return (~loadMask & value) | storeMask;
    }

    @SJC.Inline
    public static int getRange(int value, int start, int length) {
        return (value >> start) & ((1 << length) - 1);
    }

    @SJC.Inline
    public static int align(int base, int alignment) {
        return ((base + (alignment - 1)) & ~(alignment - 1));
    }

    /**
     * A utility class which converts longs in binary form into their actual binary
     * values.
     * Was created because SJC does not support binary literals
     * <i>but it turns out it's useless because function calls cannot be assigned to
     * static fields</i>.
     * 
     * <p>
     * <br>
     * Example:
     * 
     * <pre>
     *     bin(10) = 2
     *     bin(110) = 6
     *     bin(1010) = 10
     * </pre>
     *
     * <br>
     * <b>Note: This class should only be used where speed doesn't matter or for
     * debugging. It is not efficient!</b>
     * </p>
     * 
     * @param b The value to convert.
     * @return The value as an integer value.
     */
    public static int bin(long b) {
        int result = 0;
        int number_length = 0;
        while (b > 0) {
            long rightmost_bit = b % 10;
            if (rightmost_bit > 1) {
                Kernel.panic("Invalid binary literal. Only 0 and 1 are allowed.");
            }
            b /= 10;
            result <<= 1;
            result |= rightmost_bit;
            number_length += 1;
        }
        result = BitHelper.reverse32Bit(result);
        result = BitHelper.rotateRight32Bit(result, 32 - number_length);
        return result;
    }

    /// https://stackoverflow.com/a/5844096
    @SJC.Inline
    public static int rotateRight32Bit(int bits, int k) {
        return (bits >>> k) | (bits << (32 - k));
    }

    /// https://stackoverflow.com/a/9144870
    public static int reverse32Bit(int value) {
        value = ((value >> 1) & 0x55555555) | ((value & 0x55555555) << 1);
        value = ((value >> 2) & 0x33333333) | ((value & 0x33333333) << 2);
        value = ((value >> 4) & 0x0f0f0f0f) | ((value & 0x0f0f0f0f) << 4);
        value = ((value >> 8) & 0x00ff00ff) | ((value & 0x00ff00ff) << 8);
        value = ((value >> 16) & 0xffff) | ((value & 0xffff) << 16);
        return value;
    }
}