package java.lang;

import kernel.Kernel;

public class String {
    private char[] value;
    private int count;

    @SJC.Inline
    public int length() {
        return count;
    }

    @SJC.Inline
    public char charAt(int i) {
        return value[i];
    }

    public String(char[] value) {
        this.value = value;
        this.count = value.length;
    }

    public char[] toCharArray() {
        char[] copy = new char[count];
        for (int i = 0; i < count; i++) {
            copy[i] = (char) value[i];
        }
        return copy;
    }

    public String toUpperCase() {
        Kernel.panic("toUpperCase is a dummy method");
        while (true) {
        }
    }

    public byte[] toByteArray() {
        byte[] copy = new byte[count];
        for (int i = 0; i < count; i++) {
            copy[i] = (byte) value[i];
        }
        return copy;
    }

}