package kernel.interrupt;

import assembler.x86;
import kernel.Logger;
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
    public final static int IDT_BASE = 0x07E00;
    public final static int IDT_ENTRIES = 256;
    public final static int IDT_ENTRY_SIZE = 8;
    public final static int IDT_SIZE = IDT_ENTRIES * IDT_ENTRY_SIZE;
    public final static int IDT_END = IDT_BASE + IDT_SIZE;

    private static final int SEGMENT_CODE = 1;
    private static final int REQUESTED_PRIV_LEVEL_OS = 0;

    public static void initialize() {
        PIC.initialize();
        loadTable();

        SClassDesc cls = (SClassDesc) MAGIC.clssDesc("Interrupts");
        int dscAddr = MAGIC.cast2Ref(cls);

        writeTableEntry(0, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "divByZeroHandler")));
        writeTableEntry(1, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "debugHandler")));
        writeTableEntry(2, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "nmiHandler")));
        writeTableEntry(3, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "breakpointHandler")));
        writeTableEntry(4, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "overflowHandler")));
        writeTableEntry(5, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "boundRangeExceededHandler")));
        writeTableEntry(6, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "invalidOpcodeHandler")));
        writeTableEntry(7, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "reservedHandler")));
        writeTableEntry(8, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "doubleFaultHandler")));
        for (int j = 9; j < 13; j++) {
            writeTableEntry(j, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "reservedHandler")));
        }
        writeTableEntry(13, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "generalProtectionFaultHandler")));
        writeTableEntry(14, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "pageFaultHandler")));
        for (int j = 15; j < 32; j++) {
            writeTableEntry(j, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "reservedHandler")));
        }
        writeTableEntry(32, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "timerHandler"))); // IRQ 0
        writeTableEntry(33, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "keyboardHandler"))); // IRQ 1
        for (int j = 34; j < 48; j++) {
            writeTableEntry(j, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ignoreHandler"))); // IRQ 2-15
        }
        for (int j = 48; j < IDT_ENTRIES; j++) {
            writeTableEntry(j, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ignoreHandler"))); // IRQ 16-255
        }
        Logger.info("Initialized IDT");
    }

    @SJC.Inline
    public static void enable() {
        x86.sti();
        Logger.info("Enabled IDT");
    }

    @SJC.Inline
    public static void disable() {
        x86.cli();
        Logger.info("Disabled IDT");
    }

    @SJC.Inline
    public static void loadTable() {
        x86.ldit(IDT_BASE, IDT_ENTRIES * IDT_ENTRY_SIZE - 1);
        Logger.info("Load IDT (protected)");
    }

    private static int codeOffset(int classDesc, int mthdOff) {
        int code = MAGIC.rMem32(classDesc + mthdOff) + MAGIC.getCodeOff();
        return code;
    }

    private static void writeTableEntry(int i, int handlerAddr) {
        IDTEntry entry = (IDTEntry) MAGIC.cast2Struct(IDT_BASE + i * 8);
        entry.offsetLow = (short) BitHelper.getRange(handlerAddr, 0, 16);
        entry.selector = getSelector(SEGMENT_CODE, REQUESTED_PRIV_LEVEL_OS, false);
        entry.zero = 0;
        entry.typeAttr = (byte) 0x8E; // 10001110
        entry.offsetHigh = (short) BitHelper.getRange(handlerAddr, 16, 16);
    }

    /*
     * The selector is a 16-bit value that contains the following fields:
     * 0-1: Requested privilege level (0 = OS, ..., 3 = User)
     * 2: Table indicator (0 = GDT, 1 = LDT)
     * 3-13: Index of the segment descriptor in the GDT or LDT
     */
    private static short getSelector(int segment, int privLevel, boolean tableLDT) {
        int selector = 0;
        selector = BitHelper.setRange(selector, 0, 2, privLevel);
        selector = BitHelper.setFlag(selector, 2, tableLDT);
        selector = BitHelper.setRange(selector, 3, 13, segment);
        return (short) selector;
    }
}
