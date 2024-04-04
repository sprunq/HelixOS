package java.lang;

import kernel.lib.NoAllocConv;

public class Integer {

    public static byte[] BUFFER = MAGIC.toByteArray("0000000000000000000000000000", false);

    public static String toString(int i, int base) {
        for (int j = 0; j < BUFFER.length; j++) {
            BUFFER[j] = (byte) 0;
        }

        int digitCount = NoAllocConv.itoa(BUFFER, BUFFER.length, i, base);

        int newLength = digitCount;
        if (i < 0) {
            newLength += 1;
        }

        int offest = BUFFER.length - digitCount;
        byte[] chars = new byte[newLength];
        if (i < 0) {
            chars[0] = (byte) '-';
            for (int j = 0; j < digitCount; j++) {
                chars[j + 1] = BUFFER[j + offest];
            }
        } else {
            for (int j = 0; j < digitCount; j++) {
                chars[j] = BUFFER[j + offest];
            }
        }

        return new String(chars);
    }
}
