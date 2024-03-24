package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        out = new TmWriter();
        main_code();
    }

    private static void main_code() {
        out.clearScreen();

        int i = 0;
        while (true) {
            i++;
            TestAllocA e = new TestAllocA(42, 43, 44, false, "_");

            if (e.a != 42) {
                out.print("Error in object at ");
                out.print(MAGIC.cast2Ref(e), 16);
                out.println(": Values are not correct.");
                break;
            }

            if (i % 10000 == 0) {
                out.println(MAGIC.cast2Ref(e), 10);
            }
        }

        while (true) {
        }
    }

    public static void panic(String msg) {
        byte border = TmColor.set(TmColor.BLACK, TmColor.RED);
        byte textMsg = TmColor.set(TmColor.LIGHT_RED, TmColor.BLACK);
        byte textPanic = TmColor.set(TmColor.RED, TmColor.BLACK);
        int pos = 0;
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.directPrint(" PANIC: ", pos, textPanic);
        pos = TmWriter.directPrint(msg, pos, textMsg);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.newLinePos(pos);
        while (true) {
        }
    }
}
