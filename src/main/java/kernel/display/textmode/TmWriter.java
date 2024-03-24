package kernel.display.textmode;

import kernel.lib.NoAllocConv;

public class TmWriter {
    private static final TmDisplayMemory vidMem = (TmDisplayMemory) MAGIC.cast2Struct(0xB8000);

    public static final int LINE_LENGTH = 80;
    public static final int LINE_COUNT = 25;
    public static final int MAX_CURSOR = LINE_LENGTH * LINE_COUNT;

    private int cursor;
    public Brush brush;

    public TmWriter() {
        this.cursor = 0;
        this.brush = new Brush();
    }

    public void setCursor(int line, int column) {
        cursor = index1d(line, column);
        updateCursorCaret();
    }

    public void setCursor(int idx) {
        cursor = idx;
        updateCursorCaret();
    }

    public int getCursor() {
        return cursor;
    }

    public void updateCursorCaret() {
        setCursorCaret(cursor);
    }

    public void print(byte b) {
        setCharacterByte(b);
    }

    public void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            print(str.charAt(i));
        }
    }

    public void print(char[] chars) {
        for (char c : chars) {
            print(c);
        }
    }

    public void print(char c) {
        print((byte) c);
    }

    public void print(boolean b) {
        print(b ? "true" : "false");
    }

    public void print(int n, int base) {
        int max_len = MAX_CURSOR - cursor;
        int old_pos = cursor;
        cursor += NoAllocConv.itoa(MAGIC.cast2Ref(vidMem) + cursor * 2, 2, max_len, n, base);

        // Set color for the printed number
        for (int i = old_pos; i < cursor; i++) {
            vidMem.cells[i].color = brush.getColor();
        }
    }

    public void println() {
        cursor = newLinePos(cursor);
        updateCursorCaret();
    }

    public void println(byte b) {
        print(b);
        println();
    }

    public void println(char[] chars) {
        print(chars);
        println();
    }

    public void println(boolean b) {
        print(b);
        println();
    }

    public void println(char c) {
        print(c);
        println();
    }

    public void println(String str) {
        print(str);
        println();
    }

    public void clearScreen() {
        for (int i = 0; i < LINE_COUNT; i++) {
            clearLine(i);
        }
        cursor = 0;
    }

    public static void scroll_down() {
        for (int line = 0; line < LINE_COUNT - 1; line++) {
            for (int column = 0; column < LINE_LENGTH; column++) {
                int indexOld = index1d(line, column);
                int indexNew = index1d(line + 1, column);
                vidMem.cells[indexOld].character = vidMem.cells[indexNew].character;
                vidMem.cells[indexOld].color = vidMem.cells[indexNew].color;
            }
        }

        clearLine(LINE_COUNT - 1);
    }

    public static void clearLine(int line) {
        int lineStart = line * LINE_LENGTH;
        int lineEnd = lineStart + LINE_LENGTH;
        for (int i = lineStart; i < lineEnd; i++) {
            vidMem.cells[i].character = ' ';
            vidMem.cells[i].color = TmColor.set(TmColor.GREY, TmColor.BLACK);
        }
    }

    public static int directPrint(char c, int pos, int col) {
        vidMem.cells[pos].character = (byte) c;
        vidMem.cells[pos].color = (byte) col;
        return pos + 1;
    }

    public static int directPrint(String s, int pos, int col) {
        for (int i = 0; i < s.length(); i++) {
            directPrint(s.charAt(i), pos + i, col);
        }
        return pos + s.length();
    }

    public static void setCursorCaret(int pos) {
        MAGIC.wIOs8(0x3D4, (byte) 0x0F);
        MAGIC.wIOs8(0x3D5, (byte) (pos & 0xFF));
        MAGIC.wIOs8(0x3D4, (byte) 0x0E);
        MAGIC.wIOs8(0x3D5, (byte) ((pos >> 8) & 0xFF));
    }

    public static void disableCursor() {
        MAGIC.wIOs8(0x3D4, (byte) 0x0A);
        MAGIC.wIOs8(0x3D5, (byte) 0x20);
    }

    @SJC.Inline
    public static int newLinePos(int cursor) {
        return (cursor / LINE_LENGTH + 1) * LINE_LENGTH;
    }

    @SJC.Inline
    private void setCharacterByte(byte b) {
        if (cursor >= 2000) {
            scroll_down();
            cursor -= LINE_LENGTH;
        }
        vidMem.cells[cursor].character = b;
        vidMem.cells[cursor].color = brush.getColor();
        cursor += 1;
        updateCursorCaret();
    }

    @SJC.Inline
    private static int index1d(int line, int column) {
        return (line * LINE_LENGTH) + column;
    }

}
