package kernel;

import arch.x86;
import formats.fonts.Font7x8;
import gui.WindowManager;
import gui.displays.Homebar;
import gui.displays.Splashscreen;
import gui.displays.windows.Bounce;
import gui.displays.windows.Logs;
import gui.displays.windows.SystemInfo;
import kernel.display.vesa.VESAGraphics;
import kernel.display.vesa.VESAMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.ADisplay;
import kernel.hardware.PIT;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.interrupt.IDT;
import kernel.memory.GarbageCollector;
import kernel.memory.MemoryManager;
import kernel.schedeule.Schedeuler;
import kernel.tasks.Breaker;
import kernel.tasks.KeyDistributor;
import kernel.trace.Bluescreen;
import kernel.trace.SymbolResolution;
import kernel.trace.logging.Logger;
import util.vector.VecVesaMode;

public class Kernel {
    public static final int RESOLUTION = 0;

    public static ADisplay Display;

    private static WindowManager windowManager;

    public static void main() {
        MemoryManager.Initialize();
        MAGIC.doStaticInit();
        Logger.Initialize(Logger.TRACE, 100, true);
        SymbolResolution.Initialize();
        PIT.Initialize();
        IDT.Initialize();
        IDT.Enable();
        GarbageCollector.Initialize();
        Schedeuler.Initialize();

        MemoryManager.DisableGarbageCollection(); // Done manually for now

        KeyboardController.Initialize(QWERTZ.Instance);
        KeyboardController.AddListener(new Breaker());

        VecVesaMode modes = VesaQuery.AvailableModes();
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
        Display.Activate();

        windowManager = new WindowManager(Display);
        BuildSplashScreen(windowManager);
        windowManager.StaticDisplayFor(3000);

        windowManager = new WindowManager(Display);
        BuildGuiEnvironment(windowManager);

        Schedeuler.AddTask(new KeyDistributor());
        Schedeuler.AddTask(windowManager);

        Schedeuler.Run();
    }

    private static void BuildSplashScreen(WindowManager winManSplashScreen) {
        Splashscreen splash = new Splashscreen(
                0,
                0,
                0,
                Kernel.Display.Width(),
                Kernel.Display.Height());
        winManSplashScreen.AddWindow(splash);
    }

    private static void BuildGuiEnvironment(WindowManager windowManager) {
        Homebar homebar = new Homebar(
                Kernel.Display.Width(),
                Kernel.Display.Height());

        int heightMinusHomebar = Display.Height() - homebar.Height - 1;

        Logs logTextField = new Logs(
                "Log Entries",
                0,
                0,
                6,
                (int) (Display.Width() * 0.7),
                heightMinusHomebar,
                8,
                0,
                2,
                Font7x8.Instance);

        SystemInfo memMapTextField = new SystemInfo(
                "System Info",
                logTextField.X + logTextField.Width,
                0,
                5,
                Display.Width() - logTextField.Width,
                heightMinusHomebar / 2,
                8,
                0,
                2,
                Font7x8.Instance);

        Bounce bounce = new Bounce(
                logTextField.X + logTextField.Width,
                heightMinusHomebar / 2,
                6,
                Display.Width() - logTextField.Width,
                heightMinusHomebar / 2,
                "Bouncy");

        windowManager.AddWindow(homebar);
        windowManager.AddWindow(logTextField);
        windowManager.AddWindow(memMapTextField);
        windowManager.AddWindow(bounce);
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

    public static void panic(int i) {
        panic(Integer.toString(i));
    }
}
