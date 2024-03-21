package util;

public class BitHelper {
    @SJC.Inline
    public static boolean getFlag(int value, int n)
    {
        int flag = ((value >> n) & 1);
        return flag == 1;
    }

    @SJC.Inline
    public static int setFlag(int value, int n, boolean flag) {
        return ((value & ~(1 << n)) | ((flag ? 1 : 0) << n));
    }

    @SJC.Inline
    public static int setRange(int value, int start, int length, int newValue)
    {
        int highBits = (1 << length) - 1;
        int loadMask = highBits << start;
        int storeMask = (newValue & highBits) << start;
        return (~loadMask & value) | storeMask;
    }

    @SJC.Inline
    public static int getRange(int value, int start, int length)
    {
        return (value >> start) & ((1 << length) - 1);
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
