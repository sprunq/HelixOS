package kernel.interrupt;

import arch.x86;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.KeyboardController;
import kernel.trace.Bluescreen;
import kernel.trace.logging.Logger;

public class Interrupts {
    @SJC.Interrupt
    public static void IgnoreHandler() {
        Logger.Warning("INTR", "ignoreHandler");
    }

    @SJC.Interrupt
    public static void DivByZeroHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt divByZeroHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void DebugHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt debugHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void NmiHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt nmiHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void BreakpointHandler() {
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
    public static void OverflowHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt overflowHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void BoundRangeExceededHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt boundRangeExceededHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void InvalidOpcodeHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt invalidOpcodeHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void ReservedHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt reservedHandler", ebp, x86.eipForInterrupt(ebp, 0));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void DoubleFaultHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt doubleFaultHandler", ebp, x86.eipForInterrupt(ebp, 1));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void GeneralProtectionFaultHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt generalProtectionFaultHandler", ebp, x86.eipForInterrupt(ebp, 1));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void PageFaultHandler() {
        int ebp = 0;
        MAGIC.inline(0x89, 0x6D);
        MAGIC.inlineOffset(1, ebp);
        Bluescreen.Show("PANIC", "Interrupt pageFaultHandler", ebp, x86.eipForInterrupt(ebp, 1));
        while (true) {
        }
    }

    @SJC.Interrupt
    public static void TimerHandler() {
        Timer.DoTick();
        PIC.Acknowledge(0);
    }

    @SJC.Interrupt
    public static void KeyboardHandler() {
        KeyboardController.Handle();
        PIC.Acknowledge(1);
    }
}
