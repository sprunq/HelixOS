package kernel;

import arch.x86;
import formats.fonts.Font7x8;
import formats.fonts.Font9x16;
import gui.WindowManager;
import gui.displays.Homebar;
import gui.displays.Splashscreen;
import gui.displays.windows.BounceTask;
import gui.displays.windows.BounceWindow;
import gui.displays.windows.Editor;
import gui.displays.windows.Logs;
import gui.displays.windows.SystemInfo;
import kernel.display.vesa.VESAGraphics;
import kernel.display.vesa.VESAMode;
import kernel.display.vesa.VesaQuery;
import kernel.display.GraphicsContext;
import kernel.hardware.PIT;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.keyboard.layout.QWERTZ;
import kernel.hardware.mouse.MouseController;
import kernel.hardware.pci.LazyPciDeviceReader;
import kernel.hardware.pci.PciDevice;
import kernel.interrupt.IDT;
import kernel.memory.GarbageCollector;
import kernel.memory.MemoryManager;
import kernel.schedeule.Schedeuler;
import kernel.trace.Bluescreen;
import kernel.trace.SymbolResolution;
import kernel.trace.logging.Logger;
import util.vector.VecVesaMode;

public class Kernel {
    public static final int RESOLUTION = 1;

    public static GraphicsContext Display;

    public static void main() {
        Logger.LogSerial("Initializing Kernel..\n");

        MemoryManager.Initialize();
        Logger.LogSerial("Initialized Memory Manager\n");

        Logger.Initialize(Logger.TRACE, 100, true);
        Logger.Info("BOOT", "Initialized Logger");

        SymbolResolution.Initialize();
        Logger.Info("BOOT", "Initialized Symbol Resolution");

        IDT.Initialize();
        Logger.Info("BOOT", "Initialized Interrupt Descriptor Table");

        MAGIC.doStaticInit();
        Logger.Info("BOOT", "Initialized Static Initializers");

        GarbageCollector.Initialize();
        Logger.Info("BOOT", "Initialized Garbage Collector");

        MemoryManager.DisableGarbageCollection();
        Logger.Info("BOOT", "Disabled Garbage Collection");

        PrintAllPciDevices();

        PIT.Initialize();
        Logger.Info("BOOT", "Initialized PIT");

        PIT.SetRate(1000);
        Logger.Info("BOOT", "Set PIT Rate to 1000Hz");

        KeyboardController.Initialize();
        Logger.Info("BOOT", "Initialized PS2 Keyboard Controller");

        KeyboardController.SetLayout(new QWERTZ());
        Logger.Info("BOOT", "Set Keyboard Layout to QWERTZ");

        MouseController.Initialize();
        Logger.Info("BOOT", "Initialized PS2 Mouse Controller");

        IDT.Enable();
        Logger.Info("BOOT", "Enabled Interrupts");

        Schedeuler.Initialize();
        Logger.Info("BOOT", "Initialized Scheduler");

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
        Logger.Info("BOOT", "Initialized Display");

        WindowManager splash = new WindowManager(Display);
        splash.AddWindow(new Splashscreen(0, 0, 3, Display.Width(), Display.Height()));
        splash.StaticDisplayFor(0);

        Display.ClearScreen();

        WindowManager windowManager = new WindowManager(Display);
        windowManager.Register();
        Logger.Info("BOOT", "Initialized WindowManager");

        BuildGuiEnvironment(windowManager);
        Logger.Info("BOOT", "Built GUI Environment");

        Schedeuler.Run();
    }

    private static void PrintAllPciDevices() {
        Logger.Info("BOOT", "Detecting PCI Devices..");
        LazyPciDeviceReader reader = new LazyPciDeviceReader();
        while (reader.HasNext()) {
            PciDevice device = reader.Next();
            if (device == null)
                continue;
            Logger.Info("BOOT", "Found Device ".append(device.Debug()));
        }
    }

    private static void BuildGuiEnvironment(WindowManager windowManager) {
        Homebar homebar = new Homebar(
                Kernel.Display.Width(),
                Kernel.Display.Height());

        int heightMinusHomebar = Display.Height() - homebar.Height - 1;

        Editor editor = new Editor(
                "Editor",
                0,
                0,
                2,
                (int) (Display.Width() * 0.6),
                heightMinusHomebar,
                8,
                0,
                2,
                Font9x16.Instance);

        SystemInfo sysinfo = new SystemInfo(
                "System Info",
                editor.X + editor.Width,
                0,
                5,
                Display.Width() - editor.Width,
                200,
                8,
                0,
                2,
                Font7x8.Instance);

        Logs logTextField = new Logs(
                "Log Entries",
                editor.X + editor.Width,
                editor.Y + sysinfo.Height,
                3,
                Display.Width() - editor.Width,
                heightMinusHomebar - sysinfo.Height,
                8,
                0,
                2,
                Font7x8.Instance);

        BounceWindow bounce = new BounceWindow(
                0,
                0,
                9,
                300,
                300,
                "Bouncy");

        windowManager.AddWindow(homebar);
        windowManager.AddWindow(editor);
        windowManager.AddWindow(logTextField);
        windowManager.AddWindow(sysinfo);

        // new BounceTask(bounce).Register();
        // windowManager.AddWindow(bounce);
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
