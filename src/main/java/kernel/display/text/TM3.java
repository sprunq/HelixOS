package kernel.display.text;

import kernel.Env;
import kernel.memory.Memory;
import util.NoAllocConv;

public class TM3 {
    public static final TMMemory vidMem = (TMMemory) MAGIC.cast2Struct(Env.VGA_TM3_BUFFER);

    public static final int LINE_LENGTH = 80;
    public static final int LINE_COUNT = 25;
    public static final int MAX_CURSOR = LINE_LENGTH * LINE_COUNT;

    private static final int BUFFER_START = Env.VGA_TM3_BUFFER;
    private static final int BUFFER_SIZE_BYTES = MAX_CURSOR * 2;
    private static final int BUFFER_END = BUFFER_START + BUFFER_SIZE_BYTES;
    private static final int LINE_SIZE_BYTES = LINE_LENGTH * 2;

    private int cursorPos;
    private static int onScreenCursorPos;

    public TM3Brush brush;

    public TM3() {
        this.cursorPos = 0;
        this.brush = new TM3Brush();
    }

    public void setCursor(int line, int column) {
        cursorPos = index1d(line, column);
        updateCursorCaretDisplay();
    }

    public void setCursorPos(int idx) {
        cursorPos = idx;
        updateCursorCaretDisplay();
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public void print(byte b) {
        setCharacterByte(b);
        updateCursorCaretDisplay();
    }

    public void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            setCharacterByte((byte) str.charAt(i));
        }
        updateCursorCaretDisplay();
    }

    public void print(char[] chars) {
        for (char c : chars) {
            setCharacterByte((byte) c);
        }
        updateCursorCaretDisplay();
    }

    public void print(char c) {
        setCharacterByte((byte) c);
        updateCursorCaretDisplay();
    }

    public void print(boolean b) {
        print(b ? "true" : "false");
    }

    public void print(int n, int base) {
        int max_len = MAX_CURSOR - cursorPos;
        int old_pos = cursorPos;
        cursorPos += NoAllocConv.itoa(MAGIC.cast2Ref(vidMem) + cursorPos * 2, 2, max_len, n, base);

        // Set color for the printed number
        for (int i = old_pos; i < cursorPos; i++) {
            vidMem.cells[i].color = brush.getColor();
        }
        updateCursorCaretDisplay();
    }

    public void print(int n) {
        print(n, 10);
    }

    public void println() {
        cursorPos = newLinePos(cursorPos);
        shiftIfOutOfBounds();
        updateCursorCaretDisplay();
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

    public void println(int n, int base) {
        print(n, base);
        println();
    }

    public void println(int n) {
        print(n);
        println();
    }

    @SJC.Inline
    public static int directPrint(char c, int position, int color) {
        vidMem.cells[position].character = (byte) c;
        vidMem.cells[position].color = (byte) color;
        return position + 1;
    }

    public static int directPrint(String s, int position, int color) {
        for (int i = 0; i < s.length(); i++) {
            directPrint((char) s.charAt(i), position + i, color);
        }
        return position + s.length();
    }

    public static int directPrint(int n, int base, int position, int color) {
        int max_len = MAX_CURSOR - position;
        int len = NoAllocConv.itoa(MAGIC.cast2Ref(vidMem) + position * 2, 2, max_len, n, base);
        for (int i = 0; i < len; i++) {
            vidMem.cells[position + i].color = (byte) color;
        }
        return position + len;
    }

    @SJC.Inline
    public static int newLinePos(int cursor) {
        return (cursor / LINE_LENGTH + 1) * LINE_LENGTH;
    }

    /**
     * https://wiki.osdev.org/Text_Mode_Cursor#Moving_the_Cursor_2
     */
    public static void setCursorCaret(int pos) {
        MAGIC.wIOs8(0x3D4, (byte) 0x0F);
        MAGIC.wIOs8(0x3D5, (byte) (pos & 0xFF));
        MAGIC.wIOs8(0x3D4, (byte) 0x0E);
        MAGIC.wIOs8(0x3D5, (byte) ((pos >> 8) & 0xFF));
    }

    public void clearScreen() {
        clearScreenS();
        cursorPos = 0;
    }

    public static void clearScreenS() {
        byte colClear = TM3Color.set(TM3Color.GREY, TM3Color.BLACK);
        for (int i = 0; i < LINE_COUNT; i++) {
            setLine(i, (byte) ' ', colClear);
        }
    }

    public static void shift_lines() {
        for (int line = BUFFER_START; line < BUFFER_END; line += LINE_SIZE_BYTES) {
            Memory.copyBytes(line + LINE_SIZE_BYTES, line, LINE_SIZE_BYTES);
        }

        byte clearColor = TM3Color.set(TM3Color.GREY, TM3Color.BLACK);
        setLine(LINE_COUNT - 1, (byte) ' ', clearColor);
    }

    public static void setLine(int line, byte character, byte color) {
        int lineStart = line * LINE_LENGTH;
        int lineEnd = lineStart + LINE_LENGTH;
        for (int i = lineStart; i < lineEnd; i++) {
            vidMem.cells[i].character = character;
            vidMem.cells[i].color = color;
        }
    }

    public static int getLineStart(int line) {
        return line * LINE_LENGTH;
    }

    /**
     * Sets the character byte at the current cursor position in the video memory.
     * If the cursor position exceeds the maximum limit, it performs a scroll down
     * operation and adjusts the cursor position accordingly.
     */
    private void setCharacterByte(byte b) {
        shiftIfOutOfBounds();
        vidMem.cells[cursorPos].character = b;
        vidMem.cells[cursorPos].color = brush.getColor();
        cursorPos += 1;
    }

    /**
     * Updates the cursor caret position if it has changed.
     * Used to avoid unnecessary I/O operations to update the cursor position.
     */
    @SJC.Inline
    private void updateCursorCaretDisplay() {
        if (onScreenCursorPos != cursorPos) {
            setCursorCaret(cursorPos);
            onScreenCursorPos = cursorPos;
        }
    }

    @SJC.Inline
    private static int index1d(int line, int column) {
        return (line * LINE_LENGTH) + column;
    }

    /**
     * Checks if the cursor is out of bounds and shifts the lines if necessary.
     * If the cursor is out of bounds, the lines are shifted and the cursor is
     * updated accordingly.
     */
    @SJC.Inline
    private boolean shiftIfOutOfBounds() {
        if (cursorPos >= MAX_CURSOR) {
            shift_lines();
            cursorPos -= LINE_LENGTH;
            updateCursorCaretDisplay();

            return true;
        }
        return false;
    }
}
