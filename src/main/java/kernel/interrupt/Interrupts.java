package kernel.interrupt;

import kernel.Kernel;

public class Interrupts {
    @SJC.Interrupt
    public static void ignoreHandler() {
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
        Kernel.panic("Interrupt breakpointHandler");
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

    public static int timerTicks = 0;

    @SJC.Interrupt
    public static void timerHandler() {
        timerTicks += 1;
        if (timerTicks < 0) {
            timerTicks = 0;
        }
        ProgramInterruptController.acknowledge(0);
    }

    @SJC.Interrupt
    public static void keyboardHandler() {
        Kernel.panic("keyboardHandler");
        ProgramInterruptController.acknowledge(1);
    }
}
