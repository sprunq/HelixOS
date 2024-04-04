package kernel;

import gui.TextField;
import kernel.bios.BIOS;
import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.display.videomode.Ascii7x5;
import kernel.display.videomode.Ascii8x8;
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

        BIOS.activateGraphicsMode();

        TextField tf = new TextField(
                10,
                10,
                300,
                180,
                Ascii8x8.getInstance(),
                0,
                0);

        while (true) {
            int tick = SystemClock.getTick();
            String time = Integer.toString(tick, 10);
            tf.addString(time);
            tf.newLine();

            tf.draw();
            SystemClock.sleep(1000);
        }

        int x2 = 0;
        int y2 = 0;
        for (int i = 0; i < 255; i++) {
            for (int j = x2; j < x2 + 8; j++) {
                for (int j2 = y2; j2 < y2 + 8; j2++) {
                    VidWriter.putPixel(j, j2, (byte) i);
                }
            }

            x2 += 10;
            if (x2 >= 320) {
                x2 = 0;
                y2 += 10;
            }
        }

        int x = 0;
        int y = y2 + 10;
        for (int i = 0x61; i < 0x79; i++) {
            VidWriter.putChar((byte) i, Ascii7x5.getInstance(), x, y, (byte) 90);
            x += 10;
            if (x >= 320) {
                x = 0;
                y += 10;
            }
        }

        SystemClock.sleep(1000000000);

        int start = SystemClock.getTick();
        int loops = 1000;
        for (int c = 0; c < loops; c++) {
            for (int i = 0; i < 200; i++) {
                for (int j = 0; j < 320; j++) {
                    int color = (((i + j + c) / 10));
                    VidWriter.putPixel(j, i, (byte) (color % 255));
                }
            }
        }

        int end = SystemClock.getTick();
        int totalMs = SystemClock.tickToMilliseconds(end - start);
        int msPerFrame = totalMs / loops;
        BIOS.activateTextMode();
        out.print("Time: ");
        out.print(totalMs);
        out.println("ms");

        out.print("Frames: ");
        out.print(loops);
        out.println();

        out.print("ms per frame: ");
        out.print(msPerFrame);
        out.println();

        while (true) {
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
