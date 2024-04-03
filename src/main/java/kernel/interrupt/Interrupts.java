package kernel.interrupt;

import kernel.Kernel;
import kernel.bios.BIOS;
import kernel.lib.SystemClock;

public class Interrupts {
    @SJC.Interrupt
    public static void ignoreHandler() {
    }

    @SJC.Interrupt
    public static void divByZeroHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt divByZeroHandler");
    }

    @SJC.Interrupt
    public static void debugHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt debugHandler");
    }

    @SJC.Interrupt
    public static void nmiHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt nmiHandler");
    }

    @SJC.Interrupt
    public static void breakpointHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt breakpointHandler");
    }

    @SJC.Interrupt
    public static void overflowHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt overflowHandler");
    }

    @SJC.Interrupt
    public static void boundRangeExceededHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt boundRangeExceededHandler");
    }

    @SJC.Interrupt
    public static void invalidOpcodeHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt invalidOpcodeHandler");
    }

    @SJC.Interrupt
    public static void reservedHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt reservedHandler");
    }

    @SJC.Interrupt
    public static void doubleFaultHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt doubleFaultHandler");
    }

    @SJC.Interrupt
    public static void generalProtectionFaultHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt generalProtectionFaultHandler");
    }

    @SJC.Interrupt
    public static void pageFaultHandler() {
        BIOS.activateTextMode();
        Kernel.panic("Interrupt pageFaultHandler");
    }

    @SJC.Interrupt
    public static void timerHandler() {
        SystemClock.tick();
        ProgramInterruptController.acknowledge(0);
    }

    @SJC.Interrupt
    public static void keyboardHandler() {
        Kernel.panic("keyboardHandler");
        ProgramInterruptController.acknowledge(1);
    }
}
