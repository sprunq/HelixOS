package kernel.interrupt;

import arch.x86;
import kernel.Kernel;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.trace.Bluescreen;
import kernel.trace.logging.Logger;

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

        // Read registers from stack (pushed by x86.interrupt())
        int edi = MAGIC.rMem32(ebp + 4);
        int esi = MAGIC.rMem32(ebp + 8);
        int ebp2 = MAGIC.rMem32(ebp + 12);
        int esp = MAGIC.rMem32(ebp + 16);
        int ebx = MAGIC.rMem32(ebp + 20);
        int edx = MAGIC.rMem32(ebp + 24);
        int ecx = MAGIC.rMem32(ebp + 28);
        int eax = MAGIC.rMem32(ebp + 32);

        // Read old EIP from stack
        // 4 bytes before first pushed register + parameters
        int oldEip = x86.eipForInterrupt(ebp, 0);
        Bluescreen.Show("Breakpoint", "Breakpoint hit", ebp, oldEip, edi, esi, ebp2, esp, ebx, edx, ecx, eax);
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
