package util;

public class StrBuilder {
    private ByteVector strBuffer;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public StrBuilder() {
        this.strBuffer = new ByteVector(DEFAULT_CAPACITY);
        this.size = 0;
    }

    public StrBuilder(int initialCapacity) {
        this.strBuffer = new ByteVector(initialCapacity);
        this.size = 0;
    }

    public void clearKeepSize() {
        strBuffer.clearKeepSize();
        size = 0;
    }

    @SJC.Inline
    public int length() {
        return size;
    }

    public String toString() {
        byte[] buffer = new byte[size];
        for (int i = 0; i < size; i++) {
            buffer[i] = (byte) strBuffer.get(i);
        }
        return new String(buffer);
    }

    public StrBuilder append(String str) {
        if (str == null)
            str = "null";

        int len = str.length();
        for (int i = 0; i < len; i++) {
            append(str.charAt(i));
        }
        return this;
    }

    @SJC.Inline
    public StrBuilder dbg(IDebug dbg) {
        return append(dbg.dbg());
    }

    @SJC.Inline
    public StrBuilder append(byte c) {
        strBuffer.add(c);
        size++;
        return this;
    }

    @SJC.Inline
    public StrBuilder append(char c) {
        return append((byte) c);
    }

    @SJC.Inline
    public StrBuilder append(int i, int base) {
        append(Integer.toString(i, base));
        return this;
    }

    @SJC.Inline
    public StrBuilder append(int i) {
        append(Integer.toString(i, 10));
        return this;
    }

    @SJC.Inline
    public StrBuilder append(long i, int base) {
        append(Long.toString(i, base));
        return this;
    }

    @SJC.Inline
    public StrBuilder appendLine(int i, int base) {
        return append(i, base).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine(int i) {
        return append(i, 10).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine(long i, int base) {
        return append(i, base).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine(String str) {
        return append(str).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine(char c) {
        return append(c).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine(byte c) {
        return append(c).append('\n');
    }

    @SJC.Inline
    public StrBuilder appendLine() {
        return append('\n');
    }
}
