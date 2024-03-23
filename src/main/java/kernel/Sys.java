package kernel;

import kernel.display.textmode.TmWriter;
import kernel.display.textmode.TmColor;

public class Sys {
    public static void panic(String msg) {
        int pos = 0;
        pos = TmWriter.directPrintString("PANIC: ", pos, TmColor.RED);
        pos = TmWriter.directPrintString(msg, pos, TmColor.GREY);
        while (true) {
        }
    }

}