package java.lang;

import kernel.Kernel;

public class Math {
    public static int Pow(int base, int exp) {
        int result = 1;
        for (int i = 0; i < exp; i++) {
            result *= base;
        }
        return result;
    }

    public static int Abs(int n) {
        return n < 0 ? -n : n;
    }

    public static double Abs(double n) {
        return n < 0 ? -n : n;
    }

    public static long Abs(long n) {
        return n < 0 ? -n : n;
    }

    public static int Min(int a, int b) {
        return a < b ? a : b;
    }

    public static int Max(int a, int b) {
        return a > b ? a : b;
    }

    public static int Clamp(int n, int min, int max) {
        return n < min ? min : n > max ? max : n;
    }

    public static int Compress(int n, int min, int max, int newMin, int newMax) {
        return (n - min) * (newMax - newMin) / (max - min) + newMin;
    }

    // Returns -1 if n is negative, 1 if n is positive, and 0 if n is zero
    public static int Sign(int n) {
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
     */
    public static int MultiplyExact(int x, int y) {
        long r = (long) x * (long) y;
        if ((int) r != r) {
            Kernel.panic("integer overflow");
        }
        return (int) r;
    }

    public static double Sin(double n) {
        final double my_pi = 3.14159265358979323;
        n = Fmod(n, 2 * my_pi);
        if (n < 0) {
            n = 2 * my_pi - n;
        }
        int sign = 1;
        if (n > my_pi) {
            n -= my_pi;
            sign = -1;
        }
        double result = n;
        double coefficent = 3;
        for (int i = 0; i < 10; i++) {
            double pow = power(n, coefficent);
            double frac = factorial(coefficent);
            if (i % 2 == 0) {
                result = result - (pow / frac);
            } else {
                result = result + (pow / frac);
            }
            coefficent = coefficent + 2;
        }

        return sign * n;
    }

    public static double Fmod(double a, double b) {
        double frac = a / b;
        int floor = frac > 0 ? (int) frac : (int) (frac - 0.9999999999999);
        return (a - b * floor);
    }

    public static double factorial(double n) {
        if (n == 0) {
            return 1.0;
        }
        return n * (factorial(n - 1));
    }

    public static double power(double n, double power) {
        double result = n;
        for (int i = 1; i < power; i++) {
            result = n * result;
        }
        return result;
    }
}
