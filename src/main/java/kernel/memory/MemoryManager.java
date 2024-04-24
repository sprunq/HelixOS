package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.bios.call.MemMap;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    public static EmptyObject FirstEmptyObject = null;

    public static void Initialize() {
        InitEmptyObjects();
    }

    private static void InitEmptyObjects() {
        Object lastStaticObj = GetStaticAllocLast();
        int lastStaticObjAddr = MAGIC.cast2Ref(lastStaticObj);
        int continuationIndex = 0;
        do {
            MemMap.ExecMemMap(continuationIndex);
            continuationIndex = MemMap.GetMemMapContinuationIndex();
            boolean isFree = MemMap.MemMapTypeIsFree();
            long base = MemMap.GetMemMapBase();
            long length = MemMap.GetMemMapLength();

            if (!isFree
                    || base < MemoryLayout.BIOS_STKEND
                    || length <= MAGIC.getInstScalarSize("EmptyObject")
                            + MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize) {
                continue;
            }

            // Check if the segment contains static objects
            // If so, we need to create an empty object only for the remaining space;
            // otherwise, we create an empty object for the whole segment.
            boolean segmentContainsStaticObjects = base <= lastStaticObjAddr && lastStaticObjAddr <= base + length - 1;
            int emptyObjStart = segmentContainsStaticObjects ? GetPtrNextFree(lastStaticObj) : (int) base;
            int emptyObjEnd = (int) (base + length);
            int emptyObjScalarSize = emptyObjEnd - emptyObjStart - EmptyObject.RelocEntriesSize();

            EmptyObject eo = (EmptyObject) WriteObject(
                    emptyObjStart,
                    emptyObjScalarSize,
                    EmptyObject.RelocEntries(),
                    EmptyObject.Type());
            InsertIntoEmptyObjectChain(eo);

            // If the segment contains static objects, we need to update the _r_next field
            // of the last static object.
            // This can only happen once, so we can always use the last static object.
            if (segmentContainsStaticObjects) {
                MAGIC.assign(lastStaticObj._r_next, (Object) eo);
            }

        } while (continuationIndex != 0);
    }

    public static Object allocObject(int scalarSize, int relocEntries, SClassDesc type) {
        int alignScalarSize = BitHelper.Align(scalarSize, 4);
        int newObjectTotalSize = alignScalarSize + relocEntries * MAGIC.ptrSize;

        EmptyObject emptyObj = FindEmptyObjectFitting(newObjectTotalSize);
        if (emptyObj == null) {
            Kernel.panic("Out of memory");
        }

        Object newObject = null;
        if (ObjectSize(emptyObj) == newObjectTotalSize) {
            // The new object fits exactly into the empty object
            // We can replace the empty object with the new object
            RemoveFromEmptyObjectChain(emptyObj);
            newObject = WriteObject(emptyObj.AddressBottom(), scalarSize, relocEntries, type);
        } else {
            // The new object does not fit exactly into the empty object
            // We need to split the empty object
            int emptyObjectTop = emptyObj.AddressTop();
            int newObjectBottom = emptyObjectTop - newObjectTotalSize;
            newObject = WriteObject(newObjectBottom, scalarSize, relocEntries, type);
            emptyObj.ShrinkBy(newObjectTotalSize);
            InsertIntoChainAfter(emptyObj, newObject);
        }

        return newObject;
    }

    private static void RemoveFromEmptyObjectChain(EmptyObject emptyObj) {
        if (emptyObj.prevEmptyObject != null) {
            emptyObj.prevEmptyObject.nextEmptyObject = emptyObj.nextEmptyObject;
        } else {
            FirstEmptyObject = emptyObj.nextEmptyObject;
        }
        if (emptyObj.nextEmptyObject != null) {
            emptyObj.nextEmptyObject.prevEmptyObject = emptyObj.prevEmptyObject;
        }
    }

    /*
     * Adds an empty object to the chain of empty objects.
     * The chain is sorted by the address of the empty objects.
     */
    private static void InsertIntoEmptyObjectChain(EmptyObject emptyObj) {
        if (FirstEmptyObject == null) {
            FirstEmptyObject = emptyObj;
        } else {
            int emptyObjAddr = MAGIC.cast2Ref(emptyObj);
            EmptyObject t = FirstEmptyObject;
            while (t.nextEmptyObject != null) {
                if (MAGIC.cast2Ref(t.nextEmptyObject) > emptyObjAddr) {
                    break;
                }
                t = t.nextEmptyObject;
            }
            t.nextEmptyObject = emptyObj;
            emptyObj.prevEmptyObject = t;
        }
    }

    private static void InsertIntoChainAfter(EmptyObject emptyObj, Object newObject) {
        MAGIC.assign(newObject._r_next, emptyObj._r_next);
        MAGIC.assign(emptyObj._r_next, newObject);
    }

    public static int ObjectSize(Object o) {
        return o._r_scalarSize + o._r_relocEntries * MAGIC.ptrSize;
    }

    private static EmptyObject FindEmptyObjectFitting(int objectSize) {
        EmptyObject emptyObj = FirstEmptyObject;
        while (emptyObj != null) {
            if (emptyObj.Fits(objectSize)) {
                break;
            }
            emptyObj = emptyObj.nextEmptyObject;
        }
        return emptyObj;
    }

    private static int GetPtrNextFree(Object lastAlloc) {
        int lastAllocAddr = MAGIC.cast2Ref(lastAlloc);
        int lastAllocEnd = lastAllocAddr + lastAlloc._r_scalarSize;
        int ptrNextFree = BitHelper.Align(lastAllocEnd, 4);
        return ptrNextFree;
    }

    /*
     * Returns the first object in the static allocation.
     */
    @SJC.Inline
    public static Object GetStaticAllocRoot() {
        return MAGIC.cast2Obj(BOOT_IMAGE.firstHeapObject);
    }

    /**
     * Returns the last object in the static allocation.
     */
    @SJC.Inline
    public static Object GetStaticAllocLast() {
        Object obj = GetStaticAllocRoot();
        while (obj._r_next != null) {
            obj = obj._r_next;
        }
        return obj;
    }

    /**
     * Writes an object to memory.
     * 
     * @param ptrNextFree  The pointer to the next free memory location.
     * @param scalarSize   The size of the scalar fields in the object.
     * @param relocEntries The number of relocation entries in the object.
     * @param type         The type of the object.
     * @return The allocated object.
     */
    @SJC.Inline
    private static Object WriteObject(int ptrNextFree, int scalarSize, int relocEntries, SClassDesc type) {
        // Each reloc entry is a pointer
        int relocsSize = relocEntries * MAGIC.ptrSize;

        int startOfObject = ptrNextFree;
        int lengthOfObject = relocsSize + scalarSize;
        int endOfObject = startOfObject + lengthOfObject;

        // Check if the object fits into the memory. If not, panic
        if (endOfObject >= MemoryLayout.MEMORY_LIMIT) {
            Kernel.panic("Out of memory");
        }

        // Clear the memory
        Memory.Memset(startOfObject, lengthOfObject, (byte) 0);

        // cast2Obj expects the pointer to the first scalar field.
        // It needs space because relocs will be stored in front of the object
        int firstScalarField = startOfObject + relocsSize;

        Object obj = MAGIC.cast2Obj(firstScalarField);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, scalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);

        return obj;
    }

    @SJC.Inline
    public static int GetDynamicHeapStart() {
        int adr = BOOT_IMAGE.memoryStart + BOOT_IMAGE.memorySize;
        return BitHelper.Align(adr, 4);
    }
}
