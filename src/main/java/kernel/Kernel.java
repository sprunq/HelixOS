package kernel;

import gui.GUI;
import kernel.bios.BIOS;
import kernel.display.text.TmColor;
import kernel.display.text.TmWriter;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.interrupt.InterruptDescriptorTable;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        MemoryManager.initialize();
        Logger.init(20);
        InterruptDescriptorTable.initialize();
        Logger.log("Initialized IDT");
        InterruptDescriptorTable.enable();
        Logger.log("Enabled IDT");

        PIT.setRate((short) 100); // 100 hertz
        Logger.log("Set PIT rate");

        Kernel.out = new TmWriter();
        out.clearScreen();

        BIOS.activateGraphicsMode();
        Logger.log("Activated graphics");

        GUI gui = new GUI();

        while (true) {
            gui.clearDrawing();
            gui.draw();
            Timer.sleep(100);

            int ticks = Timer.getTick();
            String ticksStr = Integer.toString(ticks, 10);
            String paddedTicks = ticksStr.leftPad(5, ' ');
            gui.tfMain.addString(paddedTicks);
            gui.tfMain.addString(" ticks");
            gui.tfMain.newLine();
        }
    }

    public static void panic(String msg) {
        BIOS.activateTextMode();
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
