package kernel.interrupt;

import assembler.x86;
import rte.SClassDesc;
import util.BitHelper;

public class InterruptDescriptorTable {
    private final static int MASTER = 0x20;
    private final static int SLAVE = 0xA0;
    /*
     * The IDT starts at the lowest free address in the reserved stack region.
     * Question: Could this not lead to the IDT being overwritten by the stack?
     */
    private final static int IDT_BASE = 0x07E00;

    public static void initialize() {
        initProgrammableInterruptController();
        loadDescriptorTable(IDT_BASE, 48 * 8);

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
        writeTableEntry(32, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "timerHandler")));
        writeTableEntry(33, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "keyboardHandler")));
        for (int j = 34; j < 48; j++) {
            writeTableEntry(j, codeOffset(dscAddr, MAGIC.mthdOff("Interrupts", "ignoreHandler")));
        }
    }

    @SJC.Inline
    public static void enable() {
        x86.sti();
    }

    @SJC.Inline
    public static void disable() {
        x86.cli();
    }

    private static void loadDescriptorTable(int baseAddress, int tableLimit) {
        x86.ldit(baseAddress, tableLimit);
    }

    private static int codeOffset(int classDesc, int mthdOff) {
        int code = MAGIC.rMem32(classDesc + mthdOff) + MAGIC.getCodeOff();
        return code;
    }

    private static void writeTableEntry(int i, int handlerAddr) {

        InterruptDescTableEntry entry = (InterruptDescTableEntry) MAGIC.cast2Struct(IDT_BASE + i * 8);
        entry.offsetLow = (short) BitHelper.getRange(handlerAddr, 0, 16);
        entry.selector = 8;
        entry.zero = 0;
        entry.typeAttr = (byte) 0x8E; // 10001110
        entry.offsetHigh = (short) BitHelper.getRange(handlerAddr, 16, 16);
    }

    private static void initProgrammableInterruptController() {
        programmChip(MASTER, 0x20, 0x04); // init offset and slave config of master
        programmChip(SLAVE, 0x28, 0x02); // init offset and slave config of slave
    }

    private static void programmChip(int port, int offset, int icw3) {
        MAGIC.wIOs8(port++, (byte) 0x11); // ICW1
        MAGIC.wIOs8(port, (byte) offset); // ICW2
        MAGIC.wIOs8(port, (byte) icw3); // ICW3
    }
}
