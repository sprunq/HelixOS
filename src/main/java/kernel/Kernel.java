package kernel;

import gui.GUI;
import kernel.bios.BIOS;
import kernel.display.text.TM3Color;
import kernel.display.video.VM13;
import kernel.display.text.TM3;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.Breaker;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;
import util.StrBuilder;

public class Kernel {
    public static TM3 tmOut;
    public static GUI gui;

    public static void main() {
        MemoryManager.initialize();
        Logger.initialize(Logger.TRACE, 200);
        KeyboardController.initialize(QWERTZ.Instance);
        PIT.initialize();
        IDT.initialize();
        IDT.enable();

        Kernel.tmOut = new TM3();
        tmOut.clearScreen();

        BIOS.activateGraphicsMode();

        // The palette has to be set after graphics mode is activated
        VM13.setPalette();

        gui = new GUI();
        KeyboardController.addListener(new Breaker(), 4);
        KeyboardController.addListener(gui.MultiWindow, 3);
        KeyboardController.addListener(gui.PciDeviceReader, 2);
        KeyboardController.addListener(gui.TfMain, 1);

        StrBuilder sb = new StrBuilder();
        sb.appendLine("Phase 4")
                .appendLine()
                .appendLine("- Next win: Left CTRL + PAGE UP")
                .appendLine("- Prev win: Left CTRL + PAGE DOWN")
                .appendLine("- Break: Left CTRL + Left ALT")
                .appendLine()
                .appendLine("Pages")
                .appendLine("0: Logs")
                .appendLine("1: System MemMap")
                .appendLine("2: PCI Devices")
                .appendLine("  - Right Arrow: Next device")
                .appendLine("3: Color Palette")
                .appendLine()
                .appendLine("Man kann hier auch schreiben!");

        gui.TfMain.addString(sb.toString());

        while (true) {
            while (KeyboardController.hasNewEvent()) {
                KeyboardController.readEvent();
            }
            VM13.clearBackBuffer();
            gui.draw();
            VM13.swap();
            Timer.sleep(1000 / 20);
        }
    }

    public static void panic(String msg) {
        BIOS.activateTextMode();
        final byte colBorder = TM3Color.set(TM3Color.BLACK, TM3Color.RED);
        final byte colTextMsg = TM3Color.set(TM3Color.LIGHT_RED, TM3Color.BLACK);
        final byte colTextPanic = TM3Color.set(TM3Color.RED, TM3Color.BLACK);
        final byte clearCol = TM3Color.set(TM3Color.GREY, TM3Color.BLACK);

        TM3.setLine(0, (byte) ' ', clearCol);
        TM3.setLine(1, (byte) ' ', clearCol);
        TM3.setLine(2, (byte) ' ', clearCol);

        int pos = 0;
        pos = TM3.directPrint(' ', pos, colBorder);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint(' ', pos, colBorder);
        pos = TM3.directPrint(" PANIC: ", pos, colTextPanic);
        pos = TM3.directPrint(msg, pos, colTextMsg);
        pos = TM3.newLinePos(pos);
        pos = TM3.directPrint(' ', pos, colBorder);
        pos = TM3.newLinePos(pos);
        while (true) {
        }
    }
}
