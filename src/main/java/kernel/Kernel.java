package kernel;

import kernel.bios.BIOS;
import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.interrupt.InterruptDescriptorTable;
import kernel.interrupt.PeriodicInterruptTimer;
import kernel.lib.SystemClock;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        InterruptDescriptorTable.initialize();
        InterruptDescriptorTable.enable();
        MemoryManager.initialize();

        PeriodicInterruptTimer.setRate((short) 100); // 100 hertz

        Kernel.out = new TmWriter(true);
        out.clearScreen();

        BIOS.activateGraphicsMode();

        for (int i = 0xA0000; i < 0xA0000 + 64000; i++) {
            MAGIC.wMem8(i, (byte) (i & 0x11));
        }

        SystemClock.sleep(1000);

        while (true) {
            out.println((int) SystemClock.asSeconds());
            Kernel.printHomeBar();
            SystemClock.sleep(100);

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

    public static void printHomeBar() {
        final byte colText = TmColor.set(TmColor.WHITE, TmColor.LIGHT_BLUE);
        final byte clearCol = TmColor.set(TmColor.GREY, TmColor.LIGHT_BLUE);

        TmWriter.setLine(24, (byte) ' ', clearCol);
        int pos = TmWriter.getLineStart(24);
        int uptime = (int) SystemClock.asSeconds();
        pos = TmWriter.directPrint("TOOS v0.01", pos, colText);
        pos = TmWriter.directPrint(" | Uptime: ", pos, colText);
        pos = TmWriter.directPrint(uptime, 10, pos, colText);
        pos = TmWriter.directPrint("s", pos, colText);
    }
}
