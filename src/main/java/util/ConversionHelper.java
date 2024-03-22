package util;

public class ConversionHelper {
    public static char[] itoa(int n, int base) {
        char[] buffer = new char[33];
        // Special case for 0
        if (n == 0) {
            return new char[] { '0' };
        }

        // Prints each digit of the number but in reverse order
        int length = 0;
        while (n > 0) {
            char c = (char) ((n % base) + '0');
            n /= base;
            buffer[length] = c;
            length++;
        }

        ArrayHelper.reverse(buffer);

        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            int shifted = buffer.length - length + i;
            result[i] = buffer[shifted];
        }
        return result;
    }
}
