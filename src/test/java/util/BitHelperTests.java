package util;

import org.junit.Assert;
import org.junit.Test;

public class BitHelperTests {
    @Test
    public void TestGetFlag() {
        int data = BitHelper.bin(1100110001);
        Assert.assertEquals(true, BitHelper.getFlag(data, 0));
        Assert.assertEquals(false, BitHelper.getFlag(data, 1));
        Assert.assertEquals(false, BitHelper.getFlag(data, 2));
        Assert.assertEquals(false, BitHelper.getFlag(data, 3));
        Assert.assertEquals(true, BitHelper.getFlag(data, 4));
        Assert.assertEquals(true, BitHelper.getFlag(data, 5));
        Assert.assertEquals(false, BitHelper.getFlag(data, 6));
        Assert.assertEquals(false, BitHelper.getFlag(data, 7));
        Assert.assertEquals(true, BitHelper.getFlag(data, 8));
        Assert.assertEquals(true, BitHelper.getFlag(data, 8));
    }

    @Test
    public void TestSetFlagTrue() {
        int data = BitHelper.bin(0);
        data = BitHelper.setFlag(data, 3, true);
        Assert.assertEquals(true, BitHelper.getFlag(data, 3));
        Assert.assertEquals(8, data);
    }

    @Test
    public void TestSetFlagFalse() {
        int data = 8;
        data = BitHelper.setFlag(data, 3, false);
        Assert.assertEquals(false, BitHelper.getFlag(data, 3));
        Assert.assertEquals(0, data);
    }

    @Test
    public void TestSetFlagExistingTrue() {
        int data = BitHelper.bin(110010);
        Assert.assertEquals(data, BitHelper.setFlag(data, 1, true));
    }

    @Test
    public void TestSetFlagExistingFalse() {
        int data = BitHelper.bin(110010);
        Assert.assertEquals(data, BitHelper.setFlag(data, 3, false));
    }

    @Test
    public void TestSetRange() {
        int data = 0;
        data = BitHelper.setRange(data, 0, 3, 1);
        Assert.assertEquals(true, BitHelper.getFlag(data, 0));
        Assert.assertEquals(false, BitHelper.getFlag(data, 1));
        Assert.assertEquals(false, BitHelper.getFlag(data, 2));
        Assert.assertEquals(false, BitHelper.getFlag(data, 3));
    }

    @Test
    public void TestSetRange2() {
        int data = 0;
        data = BitHelper.setRange(data, 0, 3, 4);
        Assert.assertEquals(false, BitHelper.getFlag(data, 0));
        Assert.assertEquals(false, BitHelper.getFlag(data, 1));
        Assert.assertEquals(true, BitHelper.getFlag(data, 2));
        Assert.assertEquals(false, BitHelper.getFlag(data, 3));
    }

    @Test
    public void TestSetRangeMaskedSet() {
        int data = 0;
        data = BitHelper.setRange(data, 0, 3, 42);
        Assert.assertEquals(false, BitHelper.getFlag(data, 0));
        Assert.assertEquals(true, BitHelper.getFlag(data, 1));
        Assert.assertEquals(false, BitHelper.getFlag(data, 2));
        Assert.assertEquals(false, BitHelper.getFlag(data, 3));
        Assert.assertEquals(false, BitHelper.getFlag(data, 4));
    }

    @Test
    public void TestGetRange() {
        int data = BitHelper.bin(10001000100L);
        int expected = BitHelper.bin(10001);
        int range = BitHelper.getRange(data, 2, 5);
        Assert.assertEquals(expected, range);
    }

    @Test
    public void TestGetRangeOvershoot() {
        int data = BitHelper.bin(100);
        int expected = BitHelper.bin(1);
        int range = BitHelper.getRange(data, 2, 5);
        Assert.assertEquals(expected, range);
    }

    @Test
    public void TestGetRangeSingleBit() {
        int data = BitHelper.bin(1000);
        int range = BitHelper.getRange(data, 3, 3);
        Assert.assertEquals(1, range);
    }

    @Test
    public void TestGetRangeWholeData() {
        int data = BitHelper.bin(101010);
        int range = BitHelper.getRange(data, 0, 6);
        Assert.assertEquals(data, range);
    }

    @Test
    public void TestGetRangeEntireDataExceeding() {
        int data = BitHelper.bin(101010);
        int range = BitHelper.getRange(data, 0, 10);
        Assert.assertEquals(data, range);
    }

    @Test
    public void TestGetFlagLargeIndex() {
        int data = BitHelper.bin(101010);
        Assert.assertEquals(false, BitHelper.getFlag(data, 100));
    }

    @Test
    public void TestGetRangeSingleBitLargeIndices() {
        int data = BitHelper.bin(1000);
        int range = BitHelper.getRange(data, 100, 100);
        Assert.assertEquals(0, range);
    }

    @Test
    public void TestGetRangeEntireDataNegativeIndices() {
        int data = BitHelper.bin(101010);
        int range = BitHelper.getRange(data, -2, -1);
        Assert.assertEquals(0, range);
    }

    @Test
    public void TestSetFlagAtMaxIndex() {
        int data = BitHelper.bin(101010);
        data = BitHelper.setFlag(data, 5, true);
        Assert.assertEquals(true, BitHelper.getFlag(data, 5));
    }

    @Test
    public void TestSetFlagAtMinIndex() {
        int data = BitHelper.bin(101010);
        data = BitHelper.setFlag(data, 0, true);
        Assert.assertEquals(true, BitHelper.getFlag(data, 0));
    }

    @Test
    public void TestSetRangeFullData() {
        int data = BitHelper.bin(101010);
        data = BitHelper.setRange(data, 0, 6, 0);
        Assert.assertEquals(0, data);
    }

    @Test
    public void TestGetRangeZeroData() {
        int data = BitHelper.bin(0);
        int range = BitHelper.getRange(data, 2, 5);
        Assert.assertEquals(0, range);
    }

    @Test
    public void TestGetFlagNegativeData() {
        int data = -1;
        Assert.assertEquals(true, BitHelper.getFlag(data, 1));
    }
}
