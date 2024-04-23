package kernel;

import arch.x86;
import formats.fonts.Font7x8;
import gui.WindowManager;
import gui.displays.Homebar;
import gui.displays.Splashscreen;
import gui.displays.windows.LogTextField;
import gui.displays.windows.MemMapTextField;
import kernel.display.vesa.VESAGraphics;
import kernel.display.vesa.VESAMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.ADisplay;
import kernel.display.text.TM3;
import kernel.display.text.TM3Color;
import kernel.hardware.PIT;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.MemoryManager;
import kernel.tasks.Breaker;
import kernel.trace.Bluescreen;
import kernel.trace.SymbolResolution;
import kernel.trace.logging.Logger;
import util.vector.VecVesaMode;

public class Kernel {
    public static final int RESOLUTION = 0;

    public static ADisplay Display;

    public static void main() {
        MemoryManager.initialize();

        MAGIC.doStaticInit();
        Logger.initialize(Logger.TRACE, 100);
        SymbolResolution.initialize();
        PIT.initialize();
        IDT.initialize();
        IDT.enable();

        KeyboardController.initialize(QWERTZ.Instance);
        KeyboardController.addListener(new Breaker());

        VecVesaMode modes = VesaQuery.AvailableModes();
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
        winManSplashScreen.staticDisplayFor(2000);

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

        int heightMinusHomebar = Display.Height() - homebar.Height - 1;

        LogTextField logTextField = new LogTextField(
                "Log Entries",
                0,
                0,
                6,
                Display.Width() / 2,
                heightMinusHomebar,
                8,
                0,
                2,
                Font7x8.Instance);

        MemMapTextField memMapTextField = new MemMapTextField(
                "System Memory Map",
                Display.Width() / 2,
                0,
                5,
                Display.Width() / 2,
                heightMinusHomebar,
                8,
                0,
                2,
                Font7x8.Instance);

        windowManager.addWindow(homebar);
        windowManager.addWindow(logTextField);
        windowManager.addWindow(memMapTextField);
    }

    public static void panic(String msg) {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        int eip = x86.eipForFunction(ebp);
        Bluescreen.Show("PANIC", msg, ebp, eip);
        while (true) {
        }
    }

    public static void todo(String msg) {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        int eip = x86.eipForFunction(ebp);
        Bluescreen.Show("TODO", msg, ebp, eip);
        Timer.sleep(-1);
    }
}
