package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.trace.logging.Logger;
import rte.DynamicRuntime;
import rte.SClassDesc;

public class GarbageCollector {
    public static void Run() {
        Logger.Info("GC", "Running garbage collector");
        Logger.LogSerial("Resetting mark\n");
        ResetMark();
        Logger.LogSerial("Marking from static roots\n");
        MarkFromStaticRoots();
        Logger.LogSerial("Marking from stack\n");
        MarkFromStack();
        Logger.LogSerial("Sweeping\n");
        Sweep();
        MemoryManager.InvalidateLastAlloc();
    }

    private static void ResetMark() {
        Object o = MemoryManager.GetStaticAllocRoot();
        while (o != null) {
            o.IsUsed = false;
            o = o._r_next;
        }
    }

    @SJC.PrintCode
    private static void MarkFromStack() {
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

    private static void Sweep() {
        Object toRemove = MemoryManager.GetStaticAllocRoot();
        Object nextObject = null;
        while (toRemove != null) {
            nextObject = toRemove._r_next;

            if (toRemove.IsUsed == false) {
                if (toRemove._r_type != null && toRemove._r_type.name != null) {
                    Logger.LogSerial("Sweeping ");
                    Logger.LogSerial(toRemove._r_type.name);
                    Logger.LogSerial("\n");
                }
                MemoryManager.RemoveFromNextChain(toRemove);
                EmptyObject replacedWithEO = MemoryManager.ReplaceWithEmptyObject(toRemove);
                MemoryManager.InsertIntoEmptyObjectChain(replacedWithEO);
            }
            toRemove = nextObject;
        }
    }
}
