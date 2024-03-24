package rte;

import kernel.Kernel;
import kernel.memory.MemoryManager;

public class DynamicRuntime {
    public static Object newInstance(int scalarSize, int relocEntries, SClassDesc type) {
        return MemoryManager.allocObject(scalarSize, relocEntries, type);
    }

    public static SArray newArray(int length, int arrDim, int entrySize, int stdType, Object unitType) {
        return MemoryManager.allocArray(length, arrDim, entrySize, stdType, unitType);
    }

    public static void newMultArray(SArray[] parent, int curLevel, int destLevel, int length, int arrDim, int entrySize,
            int stdType, Object unitType) {

        Kernel.panic("newMultArray");
    }

    // probably incomplete?
    public static boolean isInstance(Object o, SClassDesc dest, boolean asCast) {
        return o._r_type == dest;
    }

    public static SIntfMap isImplementation(Object o, SIntfDesc dest, boolean asCast) {
        Kernel.panic("isImplementation");
        return null;
    }

    public static boolean isArray(SArray o, int stdType, Object unitType, int arrDim, boolean asCast) {
        Kernel.panic("isArray");
        return false;
    }

    public static void checkArrayStore(Object dest, SArray newEntry) {
        Kernel.panic("checkArrayStore");
    }
}