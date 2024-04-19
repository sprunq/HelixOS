package kernel;

import gui.WindowManager;
import gui.displays.Homebar;
import gui.displays.Splashscreen;
import gui.displays.windows.LogTextField;
import kernel.bios.call.DisplayModes;
import kernel.display.text.TM3Color;
import kernel.display.vesa.VESAGraphics;
import kernel.display.vesa.VESAMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.ADisplay;
import kernel.display.font.Font7x8;
import kernel.display.text.TM3;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;
import kernel.symbols.SymbolResolution;
import rte.SMthdBlock;
import util.logging.Logger;
import util.vector.VectorVesaMode;

public class Kernel {
    public static final int RESOLUTION = 0;

    public static ADisplay Display;

    public static void main() {
        MAGIC.doStaticInit();
        MemoryManager.initialize();
        Logger.initialize(Logger.TRACE, 200);
        SymbolResolution.initialize();
        KeyboardController.initialize(QWERTZ.Instance);
        PIT.initialize();
        IDT.initialize();
        IDT.enable();

        VectorVesaMode modes = VesaQuery.AvailableModes();
        Logger.info("VESA", "Available VESA modes:");
        for (int i = 0; i < modes.size(); i++) {
            Logger.info("VESA", modes.get(i).dbg());
        }

        VESAMode mode;
        switch (RESOLUTION) {
            case 0:
                mode = VesaQuery.GetMode(modes, 1024, 768, 32, true);
                break;
            case 1:
                mode = VesaQuery.GetMode(modes, 1440, 900, 32, true);
                break;
            default:
                panic("Invalid Resolution value");
                return;
        }

        Display = new VESAGraphics(mode);
        Display.activate();

        WindowManager winManSplashScreen = new WindowManager(Display);
        buildSplashScreen(winManSplashScreen);
        winManSplashScreen.staticDisplayFor(3000);

        WindowManager windowManager = new WindowManager(Display);
        buildGuiEnvironment(windowManager);

        int averageOver = 200;
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

    private static void buildSplashScreen(WindowManager winManSplashScreen) {
        Splashscreen splash = new Splashscreen(
                0,
                0,
                0,
                Kernel.Display.Width(),
                Kernel.Display.Height());
        winManSplashScreen.addWindow(splash);
    }

    private static void buildGuiEnvironment(WindowManager windowManager) {
        Homebar homebar = new Homebar(
                Kernel.Display.Width(),
                Kernel.Display.Height());

        LogTextField logTextField = new LogTextField(
                Display.Width() / 3,
                200,
                4,
                Display.Width() / 2,
                Kernel.Display.Height() / 2 - homebar.Height,
                8,
                0,
                2,
                Font7x8.Instance);

        LogTextField logTextField2 = new LogTextField(
                Display.Width() / 8,
                100,
                5,
                (int) (Display.Width() / 1.5),
                Kernel.Display.Height() / 2,
                8,
                0,
                2,
                Font7x8.Instance);

        windowManager.addWindow(homebar);
        windowManager.addWindow(logTextField);
        windowManager.addWindow(logTextField2);
    }

    public static void panic(String msg) {

        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        printStackTrace("PANIC", msg, ebp);
        while (true) {

        }
    }

    public static void todo(String msg) {

        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        printStackTrace("TODO", msg, ebp);
        Timer.sleep(-1);
    }

    public static void printStackTrace(String title, String message, int ebp) {
        int eip = MAGIC.rMem32(ebp + 4 * 9);
        DisplayModes.activateTextMode();
        TM3 out = new TM3();
        out.clearScreen();
        out.Brush.setFg(TM3Color.RED);
        out.println(title);
        if (message != null) {
            out.Brush.setFg(TM3Color.RED);
            out.print("Message: ");
            out.Brush.setFg(TM3Color.LIGHT_RED);
            out.println(message);
        }
        out.println();
        out.Brush.setFg(TM3Color.RED);
        out.println("Stacktrace: ");
        out.Brush.setFg(TM3Color.LIGHT_RED);
        do {
            out.print("  ");
            out.print("ebp: 0x");
            out.print(ebp, 16);
            out.print(", eip: 0x");
            out.print(eip, 16);
            out.print(", method: ");

            SMthdBlock m = SymbolResolution.resolve(eip);
            if (m != null) {
                out.print(m.namePar);
            } else {
                out.print("no method found");
            }
            out.println();
            ebp = MAGIC.rMem32(ebp);
            eip = MAGIC.rMem32(ebp + 4);
        } while (ebp <= 0x9BFFC && ebp > 0 && out.getCurrentLine() < TM3.LINE_COUNT);
    }
}
