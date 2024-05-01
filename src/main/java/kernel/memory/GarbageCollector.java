package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.trace.logging.Logger;
import util.StrBuilder;

public class GarbageCollector {

    private static StrBuilder _sb;

    private static boolean _isInitialized = false;

    public static void Initialize() {
        if (_isInitialized) {
            return;
        }
        _sb = new StrBuilder(40);
        _isInitialized = true;
        Logger.Info("GC", "Initialized");
    }

    public static boolean IsInitialized() {
        return _isInitialized;
    }

    public static void Run() {
        Logger.Info("GC", "Running");
        ResetMark();
        MarkFromStaticRoots();
        MarkFromStack();
        int collectedObjects = Sweep();
        int mergedObjects = MergeEmptyObjects();
        MemoryManager.InvalidateLastAlloc();

        _sb.ClearKeepCapacity();
        _sb.Append("Collected: ")
                .Append(collectedObjects)
                .Append(" Merged: ")
                .Append(mergedObjects);
        Logger.Info("GC", _sb.toString());
    }

    private static int MergeEmptyObjects() {
        int mergedObjects = 0;
        EmptyObject emptyObject = MemoryManager.GetEmptyObjectRoot();
        while (emptyObject != null) {
            int lastEoEnd = emptyObject.AddressTop();
            EmptyObject next = emptyObject.Next();
            // Walk along the empty objects.
            // Find empty objects that are adjacent to each other.
            // Merge them into one empty object.
            boolean c = true;
            while (next != null && c) {
                int nextEoStart = next.AddressBottom();
                int distance = nextEoStart - lastEoEnd;
                if (distance < 4) {
                    lastEoEnd = next.AddressTop();
                    mergedObjects++;
                } else {
                    c = false;
                }
                next = next.Next();
            }

            int expandBy = emptyObject.AddressTop() - lastEoEnd;
            if (expandBy > 0) {
                emptyObject.ExpandBy(expandBy);
                MAGIC.assign(emptyObject._r_next, (Object) next);
            }
            emptyObject = emptyObject.Next();
        }
        return mergedObjects;
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

    private static int Sweep() {
        int sweepedObjects = 0;
        Object toRemove = MemoryManager.GetStaticAllocRoot();
        Object nextObject = null;
        while (toRemove != null) {
            nextObject = toRemove._r_next;

            if (toRemove.IsUsed == false) {
                MemoryManager.RemoveFromNextChain(toRemove);
                EmptyObject replacedWithEO = MemoryManager.ReplaceWithEmptyObject(toRemove);
                MemoryManager.InsertIntoEmptyObjectChain(replacedWithEO);
                sweepedObjects++;
            }
            toRemove = nextObject;
        }
        return sweepedObjects;
    }
}
