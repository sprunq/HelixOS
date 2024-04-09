package kernel;

import gui.GUI;
import kernel.bios.BIOS;
import kernel.display.text.TM3Color;
import kernel.display.video.VM13;
import kernel.display.text.TM3;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;

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
        gui.tfMain.addString("  ## Welcome TO TOOS ##");
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.addString("Features:");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - Interrupts");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - Timer");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - Real Time Clock");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - Logging");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - GUI in VGA Mode 13h");
        gui.tfMain.newLine();
        gui.tfMain.addString(" - Fonts");
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.newLine();
        gui.tfMain.addString("Um Interrupts zu sehen");
        gui.tfMain.newLine();
        gui.tfMain.addString("bitte eine Taste druecken");

        while (true) {
            while (KeyboardController.hasNewEvent()) {
                KeyboardController.readEvent();
            }
            gui.clearDrawing();
            gui.draw();
            VM13.swap();
            Timer.sleep(1000 / 100);
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
