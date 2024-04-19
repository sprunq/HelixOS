package kernel.bios.call;

import kernel.MemoryLayout;
import kernel.bios.BIOS;
import util.StrBuilder;
import util.logging.Logger;

public class MemMap extends BIOS {

    public static MemMapEntry memMap(int idx) {
        Logger.trace("BIOS", new StrBuilder().append("memMap(").append(idx).append(")").toString());
        execMemMap(idx);
        return readMemMap();
    }

    public static int getMemMapContinuationIndex() {
        return Registers.EBX;
    }

    private static void execMemMap(int idx) {
        Registers.EAX = 0x0000E820;
        Registers.EDX = 0x534D4150;
        Registers.EBX = idx;
        Registers.ES = (short) (MemoryLayout.BIOS_BUFFER_MEMMAP_START >>> 4);
        Registers.EDI = MemoryLayout.BIOS_BUFFER_MEMMAP_START & 0xF;
        Registers.ECX = MemoryLayout.BIOS_BUFFER_MEMMAP_SIZE;
        rint(0x15);
    }

    private static MemMapEntry readMemMap() {
        long base = MAGIC.rMem64(MemoryLayout.BIOS_BUFFER_MEMMAP_START);
        long length = MAGIC.rMem64(MemoryLayout.BIOS_BUFFER_MEMMAP_START + 8);
        int type = MAGIC.rMem32(MemoryLayout.BIOS_BUFFER_MEMMAP_START + 16);
        return new MemMapEntry(base, length, type);
    }
}
