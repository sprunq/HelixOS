package util;

import org.junit.Assert;
import org.junit.Test;

public class ConversionHelperTests {
    @Test
    public void SimpleConversionTest() {
        char[] expected = new char[] { '2', '0' };
        char[] converted = ConversionHelper.itoa(20, 10);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void ZeroConversionTest() {
        char[] expected = new char[] { '0' };
        char[] converted = ConversionHelper.itoa(0, 10);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void BinaryConversionTest() {
        char[] expected = new char[] { '1', '0', '1', '0' };
        char[] converted = ConversionHelper.itoa(10, 2);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void HexConversionTest() {
        char[] expected = new char[] { '1', '4' };
        char[] converted = ConversionHelper.itoa(20, 16);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void OctalConversionTest() {
        char[] expected = new char[] { '2', '4' };
        char[] converted = ConversionHelper.itoa(20, 8);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void LargeDecimalConversionTest() {
        char[] expected = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        char[] converted = ConversionHelper.itoa(123456789, 10);
        Assert.assertArrayEquals(expected, converted);
    }

    @Test
    public void ZeroHexConversionTest() {
        char[] expected = new char[] { '0' };
        char[] converted = ConversionHelper.itoa(0, 16);
        Assert.assertArrayEquals(expected, converted);
    }

}
