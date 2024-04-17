package kernel;

import gui.WindowManager;
import gui.windows.LogTextField;
import gui.windows.Splashscreen;
import kernel.bios.BIOS;
import kernel.display.text.TM3Color;
import kernel.display.vesa.VESAGraphics;
import kernel.display.vesa.VESAMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.video.font.Font9x16;
import kernel.display.ADisplay;
import kernel.display.text.TM3;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;
import util.logging.Logger;
import util.vector.VectorVesaMode;

public class Kernel {
    public static TM3 TmOut;
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

        VectorVesaMode modes = VesaQuery.AvailableModes();
        for (int i = 0; i < modes.size(); i++) {
            Logger.info("VESA", modes.get(i).dbg());
        }

        VESAMode mode = VesaQuery.GetMode(modes, 1280, 768, 24, true);
        VESAGraphics Vesa = new VESAGraphics();
        Vesa.setMode(mode);
        Display = Vesa;

        Splashscreen.show(5000);

        WindowManager windowManager = new WindowManager(Display);

        buildGuiEnvironment(windowManager);

        int averageOver = 500;
        int avg = 0;
        int avgIndex = 0;
        while (true) {
            while (KeyboardController.hasNewEvent()) {
                KeyboardController.readEvent();
            }

            int startTick = Timer.getTick();

            windowManager.drawWindows();
            Display.swap();

            int endTick = Timer.getTick();
            int diff = endTick - startTick;
            avg += diff;
            avgIndex++;
            if (avgIndex >= averageOver) {
                avg /= averageOver;
                avgIndex = 0;
                int msAvg = Timer.getTickDifferenceMs(avg);
                Logger.trace("PERF", "Average draw time: ".append(msAvg).append("ms"));
                avg = 0;
            }
            Timer.sleep(1000 / 60);
        }
    }

    private static void buildGuiEnvironment(WindowManager windowManager) {
        LogTextField logTextField = new LogTextField(
                0,
                0,
                4,
                Kernel.Display.Width(),
                Kernel.Display.Height(),
                8,
                0,
                1,
                Font9x16.Instance);

        windowManager.addWindow(logTextField);
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
