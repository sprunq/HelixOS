package util;

import org.junit.Assert;
import org.junit.Test;

import kernel.Sys;

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

    @Test
    public void HexConversionManyTest() {
        for (int i = 0; i < 1_000_000; i++) {
            char[] expected = Integer.toString(i, 16).toUpperCase().toCharArray();
            char[] actual = ConversionHelper.itoa(i, 16);
            Assert.assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void HexConversionManyNegativeTest() {
        for (int i = -1_000; i < 0; i++) {
            char[] expected = Integer.toString(i, 16).toUpperCase().toCharArray();
            char[] actual = ConversionHelper.itoa(i, 16);
            Assert.assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void ItoCTest() {
        char[] expected = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < 36; i++) {
            char actual = ConversionHelper.itoc(i);
            Assert.assertEquals(expected[i], actual);
        }
    }

    @Test
    public void ItoCOutOfRangeTest() {
        char actual = ConversionHelper.itoc(37);
        Assert.assertEquals('\0', actual);
    }

    @Test
    public void ItoCOutOfRangeTest2() {
        char actual = ConversionHelper.itoc(-1);
        Assert.assertEquals('\0', actual);
    }

}
