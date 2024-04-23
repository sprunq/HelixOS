package kernel.interrupt;

import arch.x86;
import kernel.Kernel;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import util.logging.Logger;

public class Interrupts {
    @SJC.Interrupt
    public static void ignoreHandler() {
        Logger.warning("INTR", "ignoreHandler");
    }

    @SJC.Interrupt
    public static void divByZeroHandler() {
        Kernel.panic("Interrupt divByZeroHandler");
    }

    @SJC.Interrupt
    public static void debugHandler() {
        Kernel.panic("Interrupt debugHandler");
    }

    @SJC.Interrupt
    public static void nmiHandler() {
        Kernel.panic("Interrupt nmiHandler");
    }

    @SJC.Interrupt
    public static void breakpointHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        int eip = x86.ebpForInterrupt(ebp, 0);
        Kernel.printStackTrace("Breakpoint", null, ebp, eip);
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void overflowHandler() {
        Kernel.panic("Interrupt overflowHandler");
    }

    @SJC.Interrupt
    public static void boundRangeExceededHandler() {
        Kernel.panic("Interrupt boundRangeExceededHandler");
    }

    @SJC.Interrupt
    public static void invalidOpcodeHandler() {
        Kernel.panic("Interrupt invalidOpcodeHandler");
    }

    @SJC.Interrupt
    public static void reservedHandler() {
        Kernel.panic("Interrupt reservedHandler");
    }

    @SJC.Interrupt
    public static void doubleFaultHandler() {
        Kernel.panic("Interrupt doubleFaultHandler");
    }

    @SJC.Interrupt
    public static void generalProtectionFaultHandler() {
        Kernel.panic("Interrupt generalProtectionFaultHandler");
    }

    @SJC.Interrupt
    public static void pageFaultHandler() {
        Kernel.panic("Interrupt pageFaultHandler");
    }

    @SJC.Interrupt
    public static void timerHandler() {
        Timer.tick();
        PIC.acknowledge(0);
    }

    @SJC.Interrupt
    public static void keyboardHandler() {
        KeyboardController.handle();
        PIC.acknowledge(1);
    }
}
