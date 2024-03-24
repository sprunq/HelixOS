package kernel.memory;

import kernel.Kernel;
import rte.SArray;
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

    public static Object allocObject(int scalarSize, int relocEntries, SClassDesc type) {
        // Each reloc entry is a pointer
        int relocsSize = relocEntries * MAGIC.ptrSize;
        // Scalars should also be aligned to 4 bytes
        int alignedScalarSize = BitHelper.align(scalarSize, 4);

        int startOfObject = ptrNextFree;
        int lengthOfObject = relocsSize + alignedScalarSize;
        ptrNextFree = startOfObject + lengthOfObject;

        // Clear the memory
        Memory.setBytes(startOfObject, lengthOfObject, (byte) 0);

        // cast2Obj expects the pointer to the first scalar field
        int firstScalarField = startOfObject + relocsSize; // does not work

        Object obj = MAGIC.cast2Obj(firstScalarField);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, alignedScalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);

        // The last object's _r_next field should point to the current object
        if (ptrPreviousObject != 0) {
            Object lastObject = MAGIC.cast2Obj(ptrPreviousObject);
            MAGIC.assign(lastObject._r_next, obj);
        }
        ptrPreviousObject = MAGIC.cast2Ref(obj);

        return obj;
    }

    public static SArray allocArray(int length, int arrDim, int entrySize, int stdType, Object unitType) {
        int scalarSize = MAGIC.getInstScalarSize("SArray");
        int relocEntries = MAGIC.getInstRelocEntries("SArray");
        SClassDesc classDesc = (SClassDesc) MAGIC.clssDesc("SArray");

        if (arrDim != 1) {
            Kernel.panic("multidim arr not supported");
        }

        scalarSize += length * entrySize;

        SArray obj = (SArray) MemoryManager.allocObject(scalarSize, relocEntries, classDesc);
        MAGIC.assign(obj.length, length);
        MAGIC.assign(obj._r_dim, arrDim);
        MAGIC.assign(obj._r_stdType, stdType);
        MAGIC.assign(obj._r_unitType, unitType);
        return obj;
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

}
