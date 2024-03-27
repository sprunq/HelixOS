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
            copy[i] = value[i];
        }
        return copy;
    }

    public String toUpperCase() {
        Kernel.panic("toUpperCase is a dummy method");
        while (true) {
        }
    }

}