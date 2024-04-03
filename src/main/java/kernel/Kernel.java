package kernel;

import assembler.x86;
import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.interrupt.InterruptDescriptorTable;
import kernel.interrupt.Interrupts;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        InterruptDescriptorTable.initialize();
        InterruptDescriptorTable.enable();
        MemoryManager.initialize();

        Kernel.out = new TmWriter();
        out.clearScreen();

        out.println("Kernel finished");
        int oldTick = 0;
        while (true) {
            if (oldTick != Interrupts.timerTicks) {
                oldTick = Interrupts.timerTicks;
                out.println(oldTick);
            }
        }
    }

    public static void panic(String msg) {
        final byte colBorder = TmColor.set(TmColor.BLACK, TmColor.RED);
        final byte colTextMsg = TmColor.set(TmColor.LIGHT_RED, TmColor.BLACK);
        final byte colTextPanic = TmColor.set(TmColor.RED, TmColor.BLACK);
        final byte clearCol = TmColor.set(TmColor.GREY, TmColor.BLACK);

        TmWriter.setLine(0, (byte) ' ', clearCol);
        TmWriter.setLine(1, (byte) ' ', clearCol);
        TmWriter.setLine(2, (byte) ' ', clearCol);

        int pos = 0;
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.directPrint(" PANIC: ", pos, colTextPanic);
        pos = TmWriter.directPrint(msg, pos, colTextMsg);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.newLinePos(pos);
        while (true) {
        }
    }
}
