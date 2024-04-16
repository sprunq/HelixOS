package kernel;

import gui.GUI;
import kernel.bios.BIOS;
import kernel.display.text.TM3Color;
import kernel.display.vesa.VesaGraphics;
import kernel.display.vesa.VesaMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.ADisplay;
import kernel.display.text.TM3;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.Breaker;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;
import util.StrBuilder;
import util.VectorVesaMode;
import util.images.BinImageReader;
import util.images.JiJi;

public class Kernel {
    public static TM3 TmOut;
    public static GUI Gui;
    public static ADisplay Display;

    public static void main() {
        MemoryManager.initialize();
        Logger.initialize(Logger.TRACE, 200);
        KeyboardController.initialize(QWERTZ.Instance);
        PIT.initialize();
        IDT.initialize();
        IDT.enable();

        Kernel.TmOut = new TM3();
        TmOut.clearScreen();

        for (int i = 0; i < 4; i++) {
            byte b = JiJi.DATA.get(i);
            // byte c = binimp.ByteData.jiji_200[i]; // wie geht das?
            TmOut.print((int) b); // (Ignoriert das Vorzeichen)
            TmOut.print(" = ");
            TmOut.println(b & 0xFF);
        }
        return;

        VectorVesaMode modes = VesaQuery.AvailableModes();
        VesaMode mode = VesaQuery.GetMode(modes, 1024, 768, 24, true);

        VesaGraphics Vesa = new VesaGraphics();
        Vesa.setMode(mode);

        Display = Vesa;

        // int[][] bitmap = BinImageReader.decode_data(JiJi.DATA);
        panic(Integer.toString(BinImageReader.get_height(JiJi.DATA)));
        // Display.setBitmap(0, 0, bitmap);

        return;

        Gui = new GUI();

        KeyboardController.addListener(new Breaker(), 4);
        KeyboardController.addListener(Gui.MultiWindow, 3);
        KeyboardController.addListener(Gui.PciDeviceReader, 2);
        KeyboardController.addListener(Gui.TfMain, 1);

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

        Gui.TfMain.addString(sb.toString());
        Gui.drawBg();
        while (true) {
            while (KeyboardController.hasNewEvent()) {
                KeyboardController.readEvent();
            }
            Gui.drawFg();
            Timer.sleep(1000 / 30);
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

    public static void todo(String msg) {
        panic("TODO - ".append(msg));
    }
}
