package java.lang;

import kernel.lib.NoAllocConv;

public class Integer {

    public static String toString(int i) {
        byte[] buf = new byte[34];
        int length = NoAllocConv.itoa(
                MAGIC.cast2Ref(buf),
                1,
                buf.length,
                i,
                10);

        char[] chars = new char[length];
        for (int j = 0; j < length; j++) {
            chars[j] = (char) buf[j];
        }
        return new String(buf);
    }
}
