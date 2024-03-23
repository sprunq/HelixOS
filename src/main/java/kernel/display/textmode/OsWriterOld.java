package kernel.display.textmode;

public class OsWriterOld {
    private static final TmDisplayMemory vidMem = (TmDisplayMemory) MAGIC.cast2Struct(0xB8000);
    private static int vidPos;

    public static final int LINE_LENGTH = 80;
    public static final int LINE_COUNT = 25;

    @SJC.Inline
    public static void println() {
        vidPos = (vidPos / LINE_LENGTH + 1) * LINE_LENGTH;
    }

    public static void println(byte b) {
        print(b);
        println();
    }

    public static void println(boolean b) {
        print(b);
        println();
    }

    public static void println(char c) {
        print(c);
        println();
    }

    public static void println(String str) {
        print(str);
        println();
    }

    public static void println(int n) {
        print(n);
        println();
    }

    public static void clearScreen() {
        for (int i = 0; i < LINE_LENGTH * LINE_COUNT; i++) {
            vidMem.cells[i].character = ' ';
            vidMem.cells[i].color = TmColor.set(TmColor.GREY, TmColor.BLACK);
        }
        vidPos = 0;
    }

    public static void print(byte b) {
        if (vidPos < 0 || vidPos >= 2000) {
            vidPos = 0;
        }
        vidMem.cells[vidPos].character = b;
        byte color = vidMem.cells[vidPos].color;
        vidMem.cells[vidPos].color = TmColor.setFg(color, TmColor.GREY);
        vidPos += 1;
    }

    public static void print(boolean b) {
        if (b) {
            print("true");
        } else {
            print("false");
        }
    }

    public static void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            print(str.charAt(i));
        }
    }

    public static void print(char c) {
        print((byte) c);
    }

    /// Do this better...
    public static void print(int n) {
        // Special case for 0
        if (n == 0) {
            print((byte) '0');
            return;
        }

        // Prints each digit of the number but in reverse order
        int length = 0;
        while (n > 0) {
            byte rightmost_digit = (byte) (n % 10);
            n /= 10;
            print((byte) (rightmost_digit + '0'));
            length++;
        }

        // Reverse the characters
        for (int i = 0; i < length / 2; i++) {
            int x = vidPos - length + i;
            int y = vidPos - i - 1;
            byte temp = vidMem.cells[x].character;
            vidMem.cells[x].character = vidMem.cells[y].character;
            vidMem.cells[y].character = temp;
        }
    }

}
