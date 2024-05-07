package kernel.memory;

import kernel.Kernel;
import kernel.trace.logging.Logger;
import util.BitHelper;

public class VirtualMemory {
    private static final int MB4 = 1024 * 1024 * 4;
    private static final int PAGECOUNT = 1024;

    private static Object _pageTable;
    private static Object _pageDirectory;
    private static int _pageDirectoryAddr;
    private static int _pageTableAddr;

    public static void enableVirtualMemory() {
        writePageDirectory();
        Logger.Info("VirtualMemory", "PageDirectory written");
        writePageTable();
        Logger.Info("VirtualMemory", "PageTable written");
        setCR3(_pageDirectoryAddr);
        Logger.Info("VirtualMemory", "CR3 set");
        enableVirtualMemoryInternal();
        Logger.Info("VirtualMemory", "Virtual memory enabled");
    }

    public static void setCR3(int addr) {
        MAGIC.inline(0x8B, 0x45);
        MAGIC.inlineOffset(1, addr); // mov eax,[ebp+8]
        MAGIC.inline(0x0F, 0x22, 0xD8); // mov cr3,eax
    }

    public static void enableVirtualMemoryInternal() {
        MAGIC.inline(0x0F, 0x20, 0xC0); // mov eax,cr0
        MAGIC.inline(0x0D, 0x00, 0x00, 0x01, 0x80); // or eax,0x80010000
        MAGIC.inline(0x0F, 0x22, 0xC0); // mov cr0,eax
    }

    public static int getCR2() {
        int cr2 = 0;
        MAGIC.inline(0x0F, 0x20, 0xD0); // mov e/rax,cr2
        MAGIC.inline(0x89, 0x45);
        MAGIC.inlineOffset(1, cr2); // mov [ebp-4],eax
        return cr2;
    }

    private static void writePageDirectory() {
        _pageDirectory = MemoryManager.AllocateObject(
                MAGIC.getInstScalarSize("Object") + MB4 * 2,
                MAGIC.getInstRelocEntries("Object"),
                MAGIC.clssDesc("Object"));

        int freeMemPageDirectoy = MAGIC.cast2Ref(_pageDirectory) + MAGIC.getInstScalarSize("Object");
        _pageDirectoryAddr = BitHelper.AlignUp(freeMemPageDirectoy, 4096);
        if (_pageDirectory == null || _pageDirectoryAddr % 4096 != 0) {
            Kernel.panic("PageTable not aligned to 4k: ".append(Integer.toString(_pageDirectoryAddr % 4096)));
        }

        // PAGE DIRECTORY
        for (int i = 0; i < PAGECOUNT; i++) {
            int pageTableAddr = _pageTableAddr + i * 4096;
            MAGIC.wMem32(i * 4 + _pageDirectoryAddr, pageTableAddr | 0x03);
        }
    }

    private static void writePageTable() {
        _pageTable = MemoryManager.AllocateObject(
                MAGIC.getInstScalarSize("Object") + MB4 * 2,
                MAGIC.getInstRelocEntries("Object"),
                MAGIC.clssDesc("Object"));

        int freeMemPageTable = MAGIC.cast2Ref(_pageTable) + MAGIC.getInstScalarSize("Object");
        _pageTableAddr = BitHelper.AlignUp(freeMemPageTable, 4096);
        if (_pageTable == null || _pageTableAddr % 4096 != 0) {
            Kernel.panic("PageTable not aligned to 4k: ".append(Integer.toString(_pageTableAddr % 4096)));
        }

        // PAGE TABLE
        // First page is a null page. Crash
        MAGIC.wMem32(_pageTableAddr, 0);

        for (int i = 1; i < PAGECOUNT * PAGECOUNT - 1; i++) {
            MAGIC.wMem32(i * 4 + _pageTableAddr, (i << 12) | 0x03);
        }

        // Last page is a null page. Crash
        MAGIC.wMem32(((PAGECOUNT * PAGECOUNT - 1) * 4) + _pageTableAddr, 0);
    }
}
