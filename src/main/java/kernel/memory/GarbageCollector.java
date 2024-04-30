package kernel.memory;

import kernel.trace.logging.Logger;

public class GarbageCollector {
    public static void Run() {
        Logger.Info("GC", "Running garbage collector");
        ResetMark();
        MarkFromStaticRoots();
        Sweep();
    }

    private static void ResetMark() {
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != null) {
            o.IsUsed = false;
            o = o._r_next;
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

    @SJC.PrintCode
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
                MemoryManager.ReplaceWithEmptyObject(o);
            }
            o = nextObject;
        }
    }
}
