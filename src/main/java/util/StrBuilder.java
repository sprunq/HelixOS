package util;

public class StrBuilder {
    private ByteVector strBuffer;
    private static final int DEFAULT_CAPACITY = 16;

    public StrBuilder() {
        this.strBuffer = new ByteVector(DEFAULT_CAPACITY);
    }

    public StrBuilder(int initialCapacity) {
        this.strBuffer = new ByteVector(initialCapacity);
    }

    public void clearKeepCapacity() {
        strBuffer.clearKeepCapacity();
    }

    @SJC.Inline
    public int length() {
        return strBuffer.size();
    }

    public String toString() {
        byte[] buffer = strBuffer.toArray();
        return new String(buffer);
    }

    @SJC.Inline
    public StrBuilder appendLine() {
        return append('\n');
    }

    @SJC.Inline
    public StrBuilder append(byte c) {
        strBuffer.add(c);
        return this;
    }

    @SJC.Inline
    public StrBuilder appendLine(byte c) {
        return append(c).appendLine();
    }

    @SJC.Inline
    public StrBuilder dbg(IDebug dbg) {
        return append(dbg.dbg());
    }

    @SJC.Inline
    public StrBuilder dbgLine(IDebug dbg) {
        return append(dbg.dbg()).appendLine();
    }

    public StrBuilder append(String str) {
        if (str == null)
            str = "null";

        byte[] bytes = str.getBytes();
        strBuffer.addAll(bytes);
        return this;
    }

    @SJC.Inline
    public StrBuilder appendLine(String str) {
        return append(str).appendLine();
    }

    @SJC.Inline
    public StrBuilder append(char c) {
        return append((byte) c);
    }

    @SJC.Inline
    public StrBuilder appendLine(char c) {
        return append(c).appendLine();
    }

    @SJC.Inline
    public StrBuilder append(int i, int base) {
        return append(Integer.toString(i, base));
    }

    @SJC.Inline
    public StrBuilder appendLine(int i, int base) {
        return append(i, base).appendLine();
    }

    @SJC.Inline
    public StrBuilder append(int i) {
        return append(Integer.toString(i, 10));
    }

    @SJC.Inline
    public StrBuilder appendLine(int i) {
        return append(i, 10).appendLine();
    }

    @SJC.Inline
    public StrBuilder append(long i, int base) {
        return append(Long.toString(i, base));
    }

    @SJC.Inline
    public StrBuilder appendLine(long i, int base) {
        return append(i, base).appendLine();
    }

    @SJC.Inline
    public StrBuilder append(boolean b) {
        return append(b ? "true" : "false");
    }

    @SJC.Inline
    public StrBuilder appendLine(boolean b) {
        return append(b).appendLine();
    }
}
