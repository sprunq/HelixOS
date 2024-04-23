package kernel.trace;

import kernel.bios.call.DisplayModes;
import kernel.display.text.TM3;
import kernel.display.text.TM3Color;
import rte.SMthdBlock;

public class Bluescreen {
    private static final byte COL_HEADLINE = TM3Color.RED;
    private static final byte COL_MESSAGE = TM3Color.LIGHT_RED;

    public static void Show(String title, String message) {
        DisplayModes.activateTextMode();
        int pos = 0;
        pos = printHeader(pos, title, message);
    }

    public static void Show(String title, String message,
            int ebp, int eip) {
        DisplayModes.activateTextMode();
        int pos = 0;
        pos = printHeader(pos, title, message);
        pos = TM3.newLinePos(pos);
        pos = printStackTrace(pos, ebp, eip);
    }

    public static void Show(String title, String message,
            int ebp, int eip,
            int rEDI, int rESI, int rEBP, int rESP,
            int rEBX, int rEDX, int rECX, int rEAX) {
        DisplayModes.activateTextMode();

        int pos = 0;
        pos = printHeader(pos, title, message);
        pos = TM3.newLinePos(pos);
        pos = printRegisters(pos, rEDI, rESI, rEBP, rESP, rEBX, rEDX, rECX, rEAX);
        pos = TM3.newLinePos(pos);
        pos = printStackTrace(pos, ebp, eip);
    }

    private static int printHeader(int pos, String title, String message) {
        pos = TM3.directPrint(title, pos, COL_HEADLINE);
        pos = TM3.newLinePos(pos);
        if (message != null) {
            pos = TM3.directPrint("Message: ", pos, COL_HEADLINE);
            pos = TM3.directPrint(message, pos, COL_MESSAGE);
            pos = TM3.newLinePos(pos);
        }
        return pos;
    }

    private static int printRegisters(int pos, int edi, int esi, int ebp, int esp, int ebx, int edx, int ecx, int eax) {
        pos = TM3.directPrint("Registers: ", pos, COL_HEADLINE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("EDI: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(edi, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("ESI: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(esi, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("EBP: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(ebp, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("ESP: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(esp, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("EBX: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(ebx, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("EDX: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(edx, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("ECX: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(ecx, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint("EAX: 0x", pos, COL_MESSAGE);
        pos = TM3.directPrint(eax, 16, 8, '0', pos, COL_MESSAGE);
        pos = TM3.newLinePos(pos);
        return pos;
    }

    private static int printStackTrace(int pos, int ebp, int eip) {
        pos = TM3.directPrint("Stacktrace: ", pos, COL_HEADLINE);
        pos = TM3.newLinePos(pos);
        do {
            pos = TM3.directPrint("  ", pos, COL_MESSAGE);
            pos = TM3.directPrint("0x", pos, COL_MESSAGE);
            pos = TM3.directPrint(ebp, 16, 8, '0', pos, COL_MESSAGE);
            pos = TM3.directPrint(", 0x", pos, COL_MESSAGE);
            pos = TM3.directPrint(eip, 16, 8, '0', pos, COL_MESSAGE);
            pos = TM3.directPrint(", ", pos, COL_MESSAGE);

            SMthdBlock m = SymbolResolution.resolve(eip);
            if (m != null) {
                pos = TM3.directPrint(m.namePar, pos, COL_MESSAGE);
            } else {
                pos = TM3.directPrint("no method found", pos, COL_MESSAGE);
            }
            pos = TM3.newLinePos(pos);
            ebp = MAGIC.rMem32(ebp);
            eip = MAGIC.rMem32(ebp + 4);
        } while (ebp <= 0x9BFFC && ebp > 0 && TM3.getLine(pos) < TM3.LINE_COUNT);

        return pos;
    }
}
