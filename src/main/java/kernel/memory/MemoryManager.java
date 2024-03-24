package kernel.memory;

import kernel.Kernel;
import rte.SArray;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    /*
     * Gets the last Object in the chain of heap objects.
     * This is the object that was allocated last.
     * Very inefficient, but good enough for now.
     */
    public static Object getLastHeapObj() {
        Object obj = getFirstHeapObj();
        while (obj._r_next != null) {
            obj = obj._r_next;
        }
        return obj;
    }

    public static Object getFirstHeapObj() {
        return MAGIC.cast2Obj(BOOT_IMAGE.firstHeapObject);
    }

    public static int getConsumedMemory() {
        int consumed = 0;
        Object obj = getFirstHeapObj();
        while (obj != null) {
            consumed += obj._r_scalarSize;
            obj = obj._r_next;
        }
        return consumed;
    }

    public static Object allocObject(int scalarSize, int relocEntries, SClassDesc type) {
        Object lastHeapObj = getLastHeapObj();
        int lastHeapObjAddr = MAGIC.cast2Ref(lastHeapObj);
        // Align the next object to 4 bytes
        int ptrNextFree = BitHelper.align(lastHeapObjAddr + lastHeapObj._r_scalarSize, 4);

        // Each reloc entry is a pointer
        int relocsSize = relocEntries * MAGIC.ptrSize;
        // Scalars should also be aligned to 4 bytes
        int alignedScalarSize = BitHelper.align(scalarSize, 4);

        int startOfObject = ptrNextFree;
        int lengthOfObject = relocsSize + alignedScalarSize;

        // Clear the memory
        Memory.setBytes(startOfObject, lengthOfObject, (byte) 0);

        // cast2Obj expects the pointer to the first scalar field.
        // It needs space because relocs will be stored in front of the object
        int firstScalarField = startOfObject + relocsSize;

        Object obj = MAGIC.cast2Obj(firstScalarField);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, alignedScalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);

        // Link the object into the chain
        MAGIC.assign(lastHeapObj._r_next, obj);

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
