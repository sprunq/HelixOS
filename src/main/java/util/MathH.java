package util;

import kernel.Kernel;

public class MathH {
    public static int pow(int base, int exp) {
        int result = 1;
        for (int i = 0; i < exp; i++) {
            result *= base;
        }
        return result;
    }

    public static int abs(int n) {
        return n < 0 ? -n : n;
    }

    public static long abs(long n) {
        return n < 0 ? -n : n;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int clamp(int n, int min, int max) {
        return n < min ? min : n > max ? max : n;
    }

    public static int compress(int n, int min, int max, int newMin, int newMax) {
        return (n - min) * (newMax - newMin) / (max - min) + newMin;
    }

    // Returns -1 if n is negative, 1 if n is positive, and 0 if n is zero
    public static int sign(int n) {
        if (n < 0) {
            return -1;
        } else if (n > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows an {@code int}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int multiplyExact(int x, int y) {
        long r = (long) x * (long) y;
        if ((int) r != r) {
            Kernel.panic("integer overflow");
        }
        return (int) r;
    }
}
