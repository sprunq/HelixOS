package kernel.display.text;

import kernel.MemoryLayout;
import kernel.memory.Memory;
import util.NoAllocConv;

public class TM3 {
    private static final TMMemory VidMem = (TMMemory) MAGIC.cast2Struct(MemoryLayout.VGA_TEXT_BUFFER_START);

    public static final int LINE_LENGTH = 80;
    public static final int LINE_COUNT = 25;
    public static final int MAX_CURSOR = LINE_LENGTH * LINE_COUNT;

    private static final int BUFFER_START = MemoryLayout.VGA_TEXT_BUFFER_START;
    private static final int BUFFER_SIZE_BYTES = MAX_CURSOR * 2;
    private static final int BUFFER_END = BUFFER_START + BUFFER_SIZE_BYTES;
    private static final int LINE_SIZE_BYTES = LINE_LENGTH * 2;

    private int _cursorPos;
    private static int _onScreenCursorPos;

    public TM3Brush Brush;

    public TM3() {
        this._cursorPos = 0;
        this.Brush = new TM3Brush();
    }

    public void setCursor(int line, int column) {
        _cursorPos = index1d(line, column);
        updateCursorCaretDisplay();
    }

    public void set_cursorPos(int idx) {
        _cursorPos = idx;
        updateCursorCaretDisplay();
    }

    public int get_cursorPos() {
        return _cursorPos;
    }

    public int getCurrentLine() {
        return _cursorPos / LINE_LENGTH;
    }

    public void print(byte b) {
        setCharacterByte(b);
        updateCursorCaretDisplay();
    }

    public void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            setCharacterByte((byte) str.get(i));
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
        int max_len = MAX_CURSOR - _cursorPos;
        int old_pos = _cursorPos;
        _cursorPos += NoAllocConv.itoa(MAGIC.cast2Ref(VidMem) + _cursorPos * 2, 2, max_len, n, base);

        // Set color for the printed number
        for (int i = old_pos; i < _cursorPos; i++) {
            VidMem.Cells[i].Color = Brush.get_color();
        }
        updateCursorCaretDisplay();
    }

    public void print(int n) {
        print(n, 10);
    }

    public void println() {
        _cursorPos = newLinePos(_cursorPos);
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
        VidMem.Cells[position].Character = (byte) c;
        VidMem.Cells[position].Color = (byte) color;
        return position + 1;
    }

    public static int directPrint(String s, int position, int color) {
        for (int i = 0; i < s.length(); i++) {
            directPrint((char) s.get(i), position + i, color);
        }
        return position + s.length();
    }

    public static int directPrint(int n, int base, int position, int color) {
        int max_len = MAX_CURSOR - position;
        int len = NoAllocConv.itoa(MAGIC.cast2Ref(VidMem) + position * 2, 2, max_len, n, base);
        for (int i = 0; i < len; i++) {
            VidMem.Cells[position + i].Color = (byte) color;
        }
        return position + len;
    }

    public static int directPrint(int n, int base, int leftpadBy, char leftpadChar, int position, int color) {
        int max_len = MAX_CURSOR - position;
        int len = NoAllocConv.itoa(MAGIC.cast2Ref(VidMem) + position * 2, 2, max_len, n, base);

        if (len < leftpadBy) {
            int shiftCharsBy = leftpadBy - len;
            for (int i = 0; i <= len; i++) {
                VidMem.Cells[position + len + shiftCharsBy - i].Character = VidMem.Cells[position + len - i].Character;
            }

            for (int i = 0; i < shiftCharsBy; i++) {
                VidMem.Cells[position + i].Character = (byte) leftpadChar;
            }

            len += shiftCharsBy;
        }

        for (int i = 0; i < len; i++) {
            VidMem.Cells[position + i].Color = (byte) color;
        }

        return position + len;
    }

    @SJC.Inline
    public static int newLinePos(int cursor) {
        return (cursor / LINE_LENGTH + 1) * LINE_LENGTH;
    }

    @SJC.Inline
    public static int getLine(int cursor) {
        return cursor / LINE_LENGTH;
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
        _cursorPos = 0;
    }

    public static void clearScreenS() {
        byte colClear = TM3Color.set(TM3Color.GREY, TM3Color.BLACK);
        for (int i = 0; i < LINE_COUNT; i++) {
            setLine(i, (byte) ' ', colClear);
        }
    }

    public static void shift_lines() {
        for (int line = BUFFER_START; line < BUFFER_END; line += LINE_SIZE_BYTES) {
            Memory.memcopy(line + LINE_SIZE_BYTES, line, LINE_SIZE_BYTES);
        }

        byte clearColor = TM3Color.set(TM3Color.GREY, TM3Color.BLACK);
        setLine(LINE_COUNT - 1, (byte) ' ', clearColor);
    }

    public static void setLine(int line, byte character, byte color) {
        int lineStart = line * LINE_LENGTH;
        int lineEnd = lineStart + LINE_LENGTH;
        for (int i = lineStart; i < lineEnd; i++) {
            VidMem.Cells[i].Character = character;
            VidMem.Cells[i].Color = color;
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
        if (b == '\n') {
            _cursorPos = newLinePos(_cursorPos);
            shiftIfOutOfBounds();
            return;
        }
        shiftIfOutOfBounds();
        VidMem.Cells[_cursorPos].Character = b;
        VidMem.Cells[_cursorPos].Color = Brush.get_color();
        _cursorPos += 1;
    }

    /**
     * Updates the cursor caret position if it has changed.
     * Used to avoid unnecessary I/O operations to update the cursor position.
     */
    @SJC.Inline
    private void updateCursorCaretDisplay() {
        if (_onScreenCursorPos != _cursorPos) {
            setCursorCaret(_cursorPos);
            _onScreenCursorPos = _cursorPos;
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
        if (_cursorPos >= MAX_CURSOR) {
            shift_lines();
            _cursorPos -= LINE_LENGTH;
            updateCursorCaretDisplay();

            return true;
        }
        return false;
    }
}
