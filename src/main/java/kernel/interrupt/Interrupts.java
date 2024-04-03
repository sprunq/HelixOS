package kernel.interrupt;

import kernel.Kernel;

public class Interrupts {
    @SJC.Interrupt
    public static void ignoreHandler() {
    }

    @SJC.Interrupt
    public static void divByZeroHandler() {
        Kernel.panic("divByZeroHandler");
    }

    @SJC.Interrupt
    public static void debugHandler() {
        Kernel.panic("debugHandler");
    }

    @SJC.Interrupt
    public static void nmiHandler() {
        Kernel.panic("nmiHandler");
    }

    @SJC.Interrupt
    public static void breakpointHandler() {
        Kernel.panic("breakpointHandler");
    }

    @SJC.Interrupt
    public static void overflowHandler() {
        Kernel.panic("overflowHandler");
    }

    @SJC.Interrupt
    public static void boundRangeExceededHandler() {
        Kernel.panic("boundRangeExceededHandler");
    }

    @SJC.Interrupt
    public static void invalidOpcodeHandler() {
        Kernel.panic("invalidOpcodeHandler");
    }

    @SJC.Interrupt
    public static void reservedHandler() {
        Kernel.panic("reservedHandler");
    }

    @SJC.Interrupt
    public static void doubleFaultHandler() {
        Kernel.panic("doubleFaultHandler");
    }

    @SJC.Interrupt
    public static void generalProtectionFaultHandler() {
        Kernel.panic("generalProtectionFaultHandler");
    }

    @SJC.Interrupt
    public static void pageFaultHandler() {
        Kernel.panic("pageFaultHandler");
    }

    @SJC.Interrupt
    public static void timerHandler() {
        Kernel.panic("timerHandler");
    }

    @SJC.Interrupt
    public static void keyboardHandler() {
        Kernel.panic("keyboardHandler");
    }
}
