package kernel.memory;

import rte.SClassDesc;

public class MemoryManager {
    private static final BootableImage bootableImage = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    private static int ptrNextFree;
    private static int ptrLastObject;

    public static void init() {
        ptrNextFree = bootableImage.memoryStart + bootableImage.memorySize;
        ptrLastObject = 0;
    }

    public static Object alloc(int scalarSize, int relocEntries, SClassDesc type) {
        int relocsSize = relocEntries * MAGIC.ptrSize;
        int size = relocsSize + scalarSize;
        int ptrObj = ptrNextFree;

        Memory.set(ptrObj, size, (byte) 0);

        Object obj = MAGIC.cast2Obj(ptrObj);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, scalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);

        if (ptrLastObject != 0) {
            Object lastObject = MAGIC.cast2Obj(ptrLastObject);
            MAGIC.assign(lastObject._r_next, obj);
        }

        ptrLastObject = ptrObj;
        ptrNextFree += size;
        return obj;
    }

    public static int align4Byte(int x) {
        return (x + 3) & ~3;
    }
}
