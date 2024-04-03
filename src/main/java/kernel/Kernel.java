package kernel;

import kernel.bios.BIOS;
import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.display.videomode.Font;
import kernel.display.videomode.VidWriter;
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

        Kernel.out = new TmWriter();
        out.clearScreen();

        for (int ch = 0; ch < 24; ch++) {
            for (int vals = 0; vals < 13; vals++) {
                out.print(Font.getByte(ch, vals), 16);
                out.print(' ');
            }
            out.println();
        }

        // SystemClock.sleep(1000000000);
        BIOS.activateGraphicsMode();

        int x = 0;
        int y = 0;
        for (int i = 0; i < 95; i++) {
            VidWriter.font_char((byte) i, x, y, (byte) 90);
            x += 16;
            if (x >= 320) {
                x = 0;
                y += 16;
            }
        }

        SystemClock.sleep(10000000);

        int start = SystemClock.getTick();
        for (int c = 0; c < 10000; c++) {
            for (int i = 0; i < 200; i++) {
                for (int j = 0; j < 320; j++) {
                    VidWriter.putPixel(j, i, (byte) ((i + j + c) % 255));
                }
            }
        }
        int end = SystemClock.getTick();
        BIOS.activateTextMode();
        out.print("Time: ");
        out.print(SystemClock.tickToMilliseconds(end - start));
        out.println("ms");

        SystemClock.sleep(100000);

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
