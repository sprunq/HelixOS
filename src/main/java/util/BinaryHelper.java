package util;

public class BinaryHelper {
    /**
     * A utility class which converts longs in binary form into their actual binary values.
     * Was created because SJC does not support binary literals
     * <i>but it turns out it's useless because function calls cannot be assigned to static fields</i>.
     *
     * <p>
     * <br>Mom can we please have binary literals in SJC?
     * <br>Mom: No we have binary literals at home
     * </p>
     *
     * <br>Example:
     * <pre>
     *     bin(10) = 2
     *     bin(110) = 6
     *     bin(1010) = 10
     * </pre>
     *
     * <br><b>Note: This class should only be used where speed doesn't matter or for debugging. It is not efficient!</b>
     *
     *
     * @param b The value to convert.
     * @return The value as an integer value.
     */
    public static int bin(long b) {
        int result = 0;
        int number_length = 0;
        while (b > 0) {
            long rightmost_bit = b % 10;
            if (rightmost_bit > 1){
                // Number is not 0 or 1. Will throw exception once available
                while (true){}
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
}
