package kernel.display.textmode;

import util.ConversionH;

public class TmWriter {
    private static final TmDisplayMemory vidMem = (TmDisplayMemory) MAGIC.cast2Struct(0xB8000);

    public static final int LINE_LENGTH = 80;
    public static final int LINE_COUNT = 25;

    private int cursor;
    public byte color;

    public TmWriter() {
        cursor = 0;
        color = TmColor.set(TmColor.GREY, TmColor.BLACK);
    }

    public void setColor(byte fg, byte bg) {
        color = TmColor.set(fg, bg);
    }

    public void setCursor(int line, int column) {
        cursor = index1d(line, column);
    }

    @SJC.Inline
    public void println() {
        cursor = newLinePos(cursor);
    }

    @SJC.Inline
    public static int newLinePos(int cursor) {
        return (cursor / LINE_LENGTH + 1) * LINE_LENGTH;
    }

    public void print(byte b) {
        if (cursor >= 2000) {
            scroll();
            cursor -= LINE_LENGTH;
        }
        vidMem.cells[cursor].character = b;
        vidMem.cells[cursor].color = color;
        cursor += 1;
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

    public void print(int n) {
        print(n, 10);
    }

    public void print(int n, int base) {
        char[] buffer = ConversionH.itoa(n, 10);
        print(buffer);
    }

    public void print(boolean b) {
        print(b ? "true" : "false");
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

    public void println(int n) {
        print(n);
        println();
    }

    @SJC.Inline
    private static int index1d(int line, int column) {
        return (line * LINE_LENGTH) + column;
    }

    public void clearScreen() {
        for (int i = 0; i < LINE_COUNT; i++) {
            clearLine(i);
        }
        cursor = 0;
    }

    public static void scroll() {
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
}
