package rte;

import kernel.memory.MemoryManager;
import kernel.video.TextWriter;
import util.Sys;

public class DynamicRuntime {
    public static Object newInstance(int scalarSize, int relocEntries, SClassDesc type) {
        return MemoryManager.alloc(scalarSize, relocEntries, type);
    }

    public static SArray newArray(int length, int arrDim, int entrySize, int stdType, Object unitType) {
        int scalarSize = MAGIC.getInstScalarSize("SArray");
        int relocEntries = MAGIC.getInstRelocEntries("SArray");
        SClassDesc classDesc = (SClassDesc) MAGIC.clssDesc("SArray");

        if (arrDim != 1) {
            Sys.panic("multidim arr not supported");
        }

        scalarSize += length * entrySize;

        SArray obj = (SArray) newInstance(scalarSize, relocEntries, classDesc);

        MAGIC.assign(obj.length, length);
        MAGIC.assign(obj._r_dim, arrDim);
        MAGIC.assign(obj._r_stdType, stdType);
        MAGIC.assign(obj._r_unitType, unitType);

        return obj;
    }

    public static void newMultArray(SArray[] parent, int curLevel, int destLevel, int length, int arrDim, int entrySize,
            int stdType, Object unitType) {

        Sys.panic("newMultArray");
    }

    // probably incomplete?
    public static boolean isInstance(Object o, SClassDesc dest, boolean asCast) {
        return o._r_type == dest;
    }

    public static SIntfMap isImplementation(Object o, SIntfDesc dest, boolean asCast) {
        Sys.panic("isImplementation");
        return null;
    }

    public static boolean isArray(SArray o, int stdType, Object unitType, int arrDim, boolean asCast) {
        Sys.panic("isArray");
        return false;
    }

    public static void checkArrayStore(Object dest, SArray newEntry) {
        Sys.panic("checkArrayStore");
    }
}