package kernel.memory;

import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    private static final BootableImage bootableImage = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    /**
     * Represents the pointer to the next free memory location in the memory
     * manager.
     */
    private static int ptrNextFree;
    private static int ptrPreviousObject;

    public static void init() {
        ptrNextFree = getFirstAdr();
        ptrPreviousObject = 0;
    }

    public static int getFirstAdr() {
        return BitHelper.align(bootableImage.memoryStart + bootableImage.memorySize, 4);
    }

    public static Object alloc(int scalarSize, int relocEntries, SClassDesc type) {
        // Each reloc entry is a pointer
        int relocsSize = relocEntries * MAGIC.ptrSize;
        int alignedScalarSize = BitHelper.align(scalarSize, 4);

        int size = relocsSize + alignedScalarSize;
        int ptrObj = ptrNextFree;

        // null object bytes
        Memory.setBytes(ptrObj, size, (byte) 0);

        // update object internal fields
        Object newObject = setObject(ptrObj, alignedScalarSize, relocEntries, type);
        updateRefOfLastObj(newObject);

        ptrNextFree = BitHelper.align(ptrNextFree + size, 4);
        return newObject;
    }

    /**
     * Initializes an object at the given memory address and sets its intrinsic
     * values.
     */
    @SJC.Inline
    private static Object setObject(int ptrObj, int scalarSize, int relocEntries, SClassDesc type) {
        Object obj = MAGIC.cast2Obj(ptrObj);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, scalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);
        return obj;
    }

    /**
     * Updates previously created Object's <code>_r_next</code> field with a
     * reference to the <code>newObject</code>
     */
    private static void updateRefOfLastObj(Object newObject) {
        if (ptrPreviousObject != 0) {
            Object lastObject = MAGIC.cast2Obj(ptrPreviousObject);
            MAGIC.assign(lastObject._r_next, newObject);
        }
        ptrPreviousObject = MAGIC.cast2Ref(newObject);
    }
}
