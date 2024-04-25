package kernel.interrupt;

import arch.x86;
import kernel.MemoryLayout;
import kernel.trace.logging.Logger;
import rte.SClassDesc;
import util.BitHelper;

/**
 * The Interrupt Descriptor Table
 */
public class IDT {
    /*
     * The IDT can be placed somewhere in memory.
     * In my system it starts at the lowest free address in the reserved stack
     * region.
     * Question: Could this not lead to the IDT being overwritten by the stack?
     */
    private static final int SEGMENT_CODE = 1;
    private static final int REQUESTED_PRIV_LEVEL_OS = 0;

    public static void Initialize() {
        PIC.Initialize();
        LoadTableProtectedMode();

        SClassDesc cls = (SClassDesc) MAGIC.clssDesc("Interrupts");
        int dscAddr = MAGIC.cast2Ref(cls);

        WriteTableEntry(0, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "DivByZeroHandler")));
        WriteTableEntry(1, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "DebugHandler")));
        WriteTableEntry(2, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "NmiHandler")));
        WriteTableEntry(3, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "BreakpointHandler")));
        WriteTableEntry(4, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "OverflowHandler")));
        WriteTableEntry(5, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "BoundRangeExceededHandler")));
        WriteTableEntry(6, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "InvalidOpcodeHandler")));
        WriteTableEntry(7, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ReservedHandler")));
        WriteTableEntry(8, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "DoubleFaultHandler")));
        for (int j = 9; j < 13; j++) {
            WriteTableEntry(j, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ReservedHandler")));
        }
        WriteTableEntry(13, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "GeneralProtectionFaultHandler")));
        WriteTableEntry(14, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "PageFaultHandler")));
        for (int j = 15; j < 32; j++) {
            WriteTableEntry(j, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ReservedHandler")));
        }
        WriteTableEntry(32, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "TimerHandler"))); // IRQ 0
        WriteTableEntry(33, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "KeyboardHandler"))); // IRQ 1
        for (int j = 34; j < 48; j++) {
            WriteTableEntry(j, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "IgnoreHandler"))); // IRQ 2-15
        }
        for (int j = 48; j < MemoryLayout.IDT_ENTRIES; j++) {
            WriteTableEntry(j, CodeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "IgnoreHandler"))); // IRQ 16-255
        }
        Logger.Info("IDT", "Initialized");
    }

    @SJC.Inline
    public static void Enable() {
        x86.sti();
    }

    @SJC.Inline
    public static void Disable() {
        x86.cli();
    }

    @SJC.Inline
    public static void LoadTableProtectedMode() {
        x86.ldit(MemoryLayout.IDT_BASE, MemoryLayout.IDT_SIZE - 1);
    }

    @SJC.Inline
    public static void LoadTableRealMode() {
        x86.ldit(0, 1023);
    }

    private static int CodeOffset(int classDesc, int mthdOff) {
        int code = MAGIC.rMem32(classDesc + mthdOff) + MAGIC.getCodeOff();
        return code;
    }

    private static void WriteTableEntry(int i, int handlerAddr) {
        IDTEntry entry = (IDTEntry) MAGIC.cast2Struct(MemoryLayout.IDT_BASE + i * 8);
        entry.offsetLow = (short) BitHelper.GetRange(handlerAddr, 0, 16);
        entry.selector = GetSelector(SEGMENT_CODE, REQUESTED_PRIV_LEVEL_OS, false);
        entry.zero = 0;
        entry.typeAttr = (byte) 0x8E; // 10001110
        entry.offsetHigh = (short) BitHelper.GetRange(handlerAddr, 16, 16);
    }

    /*
     * The selector is a 16-bit value that contains the following fields:
     * 0-1: Requested privilege level (0 = OS, ..., 3 = User)
     * 2: Table indicator (0 = GDT, 1 = LDT)
     * 3-13: Index of the segment descriptor in the GDT or LDT
     */
    private static short GetSelector(int segment, int privLevel, boolean tableLDT) {
        int selector = 0;
        selector = BitHelper.SetRange(selector, 0, 2, privLevel);
        selector = BitHelper.SetFlag(selector, 2, tableLDT);
        selector = BitHelper.SetRange(selector, 3, 13, segment);
        return (short) selector;
    }
}
