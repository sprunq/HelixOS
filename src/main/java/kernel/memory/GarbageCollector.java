package kernel.memory;

import kernel.MemoryLayout;
import kernel.trace.logging.Logger;
import rte.DynamicRuntime;
import rte.SClassDesc;

public class GarbageCollector {
    public static void Run() {
        Logger.Info("GC", "Running garbage collector");
        ResetMark();
        MarkFromStaticRoots();
        MarkFromStack();
        Sweep();
    }

    private static void ResetMark() {
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != null) {
            o.IsUsed = false;
            o = o._r_next;
        }
    }

    private static void MarkFromStack() {
        int varAtTopOfStack = 0;
        int topOfStackAddr = MAGIC.addr(varAtTopOfStack);
        int currentSlot = MemoryLayout.PROGRAM_STACK_COMPILER_TOP;
        while (currentSlot > topOfStackAddr) {
            int c = MAGIC.rMem32(currentSlot);
            Object o = MAGIC.cast2Obj(c);
            if (DynamicRuntime.isInstance(o, (SClassDesc) MAGIC.clssDesc("Object"), false)) {
                MarkRecursive(o);
            }
            currentSlot -= MAGIC.ptrSize;
        }
    }

    private static void MarkFromStaticRoots() {
        Object end = MemoryManager.GetDynamicAllocRoot();
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != end) {
            MarkRecursive(o);
            o = o._r_next;
        }
    }

    private static void MarkRecursive(Object o) {
        if (o == null || o.IsUsed == true) {
            return;
        }

        o.IsUsed = true;

        // Skip _r_type and _r_next
        for (int relocIndex = 2; relocIndex < o._r_relocEntries; relocIndex++) {
            Object entry = o.ReadRelocEntry(relocIndex);
            if (entry != null) {
                MarkRecursive(entry);
            }
        }
    }

    private static void Sweep() {
        Object o = MemoryManager.GetStaticAllocRoot();
        Object nextObject = null;
        while (o != null) {
            nextObject = o._r_next;
            if (o.IsUsed == false) {
                // Repalce with many small objects now and merge later
                EmptyObject eo = MemoryManager.ReplaceWithEmptyObject(o);
                MemoryManager.InsertIntoEmptyObjectChain(eo);
            }
            o = nextObject;
        }
    }
}
