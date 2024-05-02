package kernel.memory;

import kernel.MemoryLayout;
import kernel.trace.logging.Logger;
import util.StrBuilder;

public class GarbageCollector {

    private static boolean _isInitialized = false;

    private static int _gcCylce = 0;

    public static int InfoLastRunMarked = 0;
    public static int InfoLastRunBytesCollected = 0;
    public static int InfoLastRunCompacted = 0;

    public static void Initialize() {
        if (_isInitialized) {
            return;
        }
        _isInitialized = true;
        Logger.Info("GC", "Initialized");
    }

    public static boolean IsInitialized() {
        return _isInitialized;
    }

    public static void Run() {
        int objects = ResetMark();
        MarkFromStaticRoots();
        MarkFromStack();
        InfoLastRunBytesCollected = Sweep();
        MemoryManager.InvalidateLastAlloc();
        InfoLastRunCompacted = CompactIfNeeded();
        InfoLastRunMarked = objects - InfoLastRunBytesCollected;

        boolean log = false;
        if (log) {
            StrBuilder sb = new StrBuilder(30);
            sb.Append("Freed: ").Append(InfoLastRunBytesCollected).Append(" bytes");
            if (ShouldCompact()) {
                sb.Append(", ").Append("Compacted EOs: ").Append(InfoLastRunCompacted);
            }
            Logger.Info("GC", sb.toString());
        }

        _gcCylce++;

    }

    private static int CompactIfNeeded() {
        if (ShouldCompact()) {
            return MemoryManager.CompactEmptyObjects();
        }
        return 0;
    }

    private static boolean ShouldCompact() {
        return _gcCylce % 7 == 0;
    }

    private static int ResetMark() {
        int objects = 0;
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != null) {
            o.IsUsed = false;
            o = o._r_next;
            objects++;
        }
        return objects;
    }

    /*
     * Mark all objects that are reachable from the stack.
     * This causes some issues atm so its basically ignored by only calling the gc
     * in situations where the stack is not used.
     */
    private static void MarkFromStack() {
        // TODO: Push all registers to stack
        int varAtTopOfStack = 0;
        int scanUntil = MAGIC.addr(varAtTopOfStack);
        for (int i = MemoryLayout.PROGRAM_STACK_COMPILER_TOP; i > scanUntil; i -= MAGIC.ptrSize) {
            int mem = MAGIC.rMem32(i);
            if (PointsToHeap(mem)) {
                Object o = MAGIC.cast2Obj(mem);
                MarkRecursive(o);
            }
        }
    }

    // brute force ftw
    private static boolean PointsToHeap(int addr) {
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != null) {
            if (MAGIC.cast2Ref(o) == addr) {
                return true;
            }
            o = o._r_next;
        }
        return false;
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

        if (!(o instanceof Object)) {
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

    private static int Sweep() {
        int sweepedBytes = 0;
        Object toRemove = MemoryManager.GetStaticAllocRoot();
        Object nextObject = null;
        while (toRemove != null) {
            nextObject = toRemove._r_next;

            if (toRemove.IsUsed == false) {
                sweepedBytes += MemoryManager.ObjectSize(toRemove);
                MemoryManager.RemoveFromNextChain(toRemove);
                EmptyObject replacedWithEO = MemoryManager.ReplaceWithEmptyObject(toRemove);
                MemoryManager.InsertIntoEmptyObjectChain(replacedWithEO);
            }
            toRemove = nextObject;
        }
        return sweepedBytes;
    }
}
