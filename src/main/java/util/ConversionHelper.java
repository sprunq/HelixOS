package util;

import kernel.Sys;

public class ConversionHelper {

    public static char[] itoa(int n, int base) {
        if (base < 2 || base > 36) {
            Sys.panic("ConversionHelper: requested base out of range");
        }
        char[] buffer = new char[33];
        // Special case for 0
        if (n == 0) {
            char[] b = new char[1];
            b[0] = '0';
            return b;
        }
        boolean negative = n < 0;
        if (negative) {
            n = -n;
        }

        // Prints each digit of the number but in reverse order
        int length = 0;
        while (n > 0) {
            char c = itoc(n % base);
            n /= base;
            buffer[length] = c;
            length++;
        }

        ArrayHelper.reverse(buffer);
        int offsetIfNegative = negative ? 1 : 0;
        char[] result = new char[length + offsetIfNegative];
        if (negative) {
            result[0] = '-';
        }
        for (int i = 0; i < length; i++) {
            int shifted = buffer.length - length + i;
            result[i + offsetIfNegative] = buffer[shifted];
        }

        return result;
    }

    /*
     * Converts a byte to a character.
     * 0..9 -> '0'..'9'
     * 10..36 -> 'A'..'Z'
     * Other values -> '\0'
     */
    @SJC.Inline
    public static char itoc(int n) {
        if (n >= 0 && n <= 9) {
            return (char) (n + '0');
        } else if (n >= 10 && n <= 36) {
            return (char) (n - 10 + 'A');
        } else {
            return '\0';
        }
    }
}
