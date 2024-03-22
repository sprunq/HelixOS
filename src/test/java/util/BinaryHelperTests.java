package util;

import org.junit.Assert;
import org.junit.Test;

import kernel.video.TextWriter;

import static util.BinaryHelper.bin;

public class BinaryHelperTests {
    private static void testAllBinaryCombinations(int n, boolean[] bits, int i) {
        if (i == n) {
            String bitString = joinBits(bits);
            long binaryReprAsLong = Long.parseLong(bitString);
            long decNumberTruth = Long.parseLong(bitString, 2);

            long decNumberConverted = bin(binaryReprAsLong);
            Assert.assertEquals(decNumberTruth, decNumberConverted);
            return;
        }

        bits[i] = false;
        testAllBinaryCombinations(n, bits, i + 1);

        bits[i] = true;
        testAllBinaryCombinations(n, bits, i + 1);
    }

    private static String joinBits(boolean[] aArr) {
        char[] chars = new char[aArr.length];
        for (int i = 0; i < aArr.length; i++) {
            chars[i] = aArr[i] ? '1' : '0';
        }
        return new String(chars);
    }

    @Test
    public void binaryLiteralZeroTest() {
        Assert.assertEquals(0, bin(0));
    }

    @Test
    public void binaryLiteralOneTest() {
        Assert.assertEquals(1, bin(1));
    }

    @Test
    public void binaryLiteralAllPossibleCombinationsTest() {
        // 10000000000000000000L is limit. Only have to test up to 19 bits
        int n = 19;
        boolean[] bits = new boolean[n];
        testAllBinaryCombinations(n, bits, 0);
    }

}
