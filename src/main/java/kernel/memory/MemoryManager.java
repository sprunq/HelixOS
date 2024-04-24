package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.bios.call.MemMap;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    public static EmptyObject _firstEmptyObject = null;

    /*
     * The DynamicAllocRoot marks the beginning of the dynamic allocation.
     * It is allocated as the first object after the static allocation,
     * and marks the beginning of the dynamic allocations.
     */
    public static DynamicAllocRoot _dynamicAllocRoot = null;

    public static void Initialize() {
        InitEmptyObjects();
        if (_firstEmptyObject == null) {
            Kernel.panic("No viable memory regions found");
        }

        _dynamicAllocRoot = (DynamicAllocRoot) allocObject(
                MAGIC.getInstScalarSize("DynamicAllocRoot"),
                MAGIC.getInstRelocEntries("DynamicAllocRoot"),
                (SClassDesc) MAGIC.clssDesc("DynamicAllocRoot"));
    }

    /*
     * Returns the first object in the static allocation.
     */
    @SJC.Inline
    public static Object GetStaticAllocRoot() {
        return MAGIC.cast2Obj(BOOT_IMAGE.firstHeapObject);
    }

    /*
     * Returns the first object in the dynamic allocation.
     */
    @SJC.Inline
    public static DynamicAllocRoot GetDynamicAllocRoot() {
        return _dynamicAllocRoot;
    }

    /*
     * Returns the first empty object.
     */
    @SJC.Inline
    public static EmptyObject GetFirstEmptyObject() {
        return _firstEmptyObject;
    }

    @SJC.Inline
    public static int ObjectSize(Object o) {
        return o._r_scalarSize + o._r_relocEntries * MAGIC.ptrSize;
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
            Kernel.panic("trace - perfect fit");
            // The new object fits exactly into the empty object
            // We can replace the empty object with the new object
            Object objPointingToEmpty = ObjectPointingTo(emptyObj);
            if (objPointingToEmpty == null) {
                Kernel.panic("Empty object not found in chain");
            }
            RemoveFromEmptyObjectChain(emptyObj);
            // The empty object will be overwritten
            newObject = WriteObject(emptyObj.AddressBottom(), scalarSize, relocEntries, type);
            InsertIntoNextChain(objPointingToEmpty, newObject);
        } else if (emptyObj.UnreservedScalarSize() > newObjectTotalSize) {
            // The new object does not fit exactly into the empty object
            // We need to split the empty object
            int emptyObjectTop = emptyObj.AddressTop();
            int newObjectBottom = emptyObjectTop - newObjectTotalSize;
            if (newObjectBottom < emptyObj.AddressBottom()) {
                Kernel.panic("trace - new object bottom is below empty object bottom");
            }
            newObject = WriteObject(newObjectBottom, scalarSize, relocEntries, type);
            emptyObj.ShrinkBy(newObjectTotalSize);
            InsertIntoNextChain(emptyObj, newObject);
        }

        if (newObject == null) {
            Kernel.panic("Failed to allocate object");
        }
        return newObject;
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
            int emptyObjStart = segmentContainsStaticObjects ? NextAllocFrame(lastStaticObj) : (int) base;
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

    /**
     * Writes an object to memory.
     * 
     * @param ptrNextFree  The pointer to the next free memory location.
     * @param scalarSize   The size of the scalar fields in the object.
     * @param relocEntries The number of relocation entries in the object.
     * @param type         The type of the object.
     * @return The allocated object.
     */
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

    /*
     * Adds an empty object to the chain of empty objects.
     * The chain is sorted by the address of the empty objects.
     */
    private static void InsertIntoEmptyObjectChain(EmptyObject emptyObj) {
        if (_firstEmptyObject == null) {
            _firstEmptyObject = emptyObj;
        } else {
            int emptyObjAddr = MAGIC.cast2Ref(emptyObj);
            EmptyObject t = _firstEmptyObject;
            while (t.NextEmptyObject != null) {
                if (MAGIC.cast2Ref(t.NextEmptyObject) > emptyObjAddr) {
                    break;
                }
                t = t.NextEmptyObject;
            }
            t.NextEmptyObject = emptyObj;
            emptyObj.PrevEmptyObject = t;
        }
    }

    @SJC.Inline
    private static void RemoveFromEmptyObjectChain(EmptyObject emptyObj) {
        if (emptyObj.PrevEmptyObject != null) {
            emptyObj.PrevEmptyObject.NextEmptyObject = emptyObj.NextEmptyObject;
        } else {
            _firstEmptyObject = emptyObj.NextEmptyObject;
        }
        if (emptyObj.NextEmptyObject != null) {
            emptyObj.NextEmptyObject.PrevEmptyObject = emptyObj.PrevEmptyObject;
        }
    }

    @SJC.Inline
    private static void InsertIntoNextChain(Object insertAfter, Object o) {
        MAGIC.assign(o._r_next, insertAfter._r_next);
        MAGIC.assign(insertAfter._r_next, o);
    }

    @SJC.Inline
    private static EmptyObject FindEmptyObjectFitting(int objectSize) {
        EmptyObject emptyObj = _firstEmptyObject;
        while (emptyObj != null) {
            if (emptyObj.UnreservedScalarSize() >= objectSize
                    || ObjectSize(emptyObj) == objectSize) {
                return emptyObj;
            }
            emptyObj = emptyObj.NextEmptyObject;
        }
        return null;
    }

    /*
     * Returns the pointer to the next free memory location after the given object.
     * The pointer is aligned to 4 bytes.
     */
    @SJC.Inline
    private static int NextAllocFrame(Object o) {
        int lastAllocAddr = MAGIC.cast2Ref(o);
        int lastAllocEnd = lastAllocAddr + o._r_scalarSize;
        int ptrNextFree = BitHelper.Align(lastAllocEnd, 4);
        return ptrNextFree;
    }

    @SJC.Inline
    public static Object GetStaticAllocLast() {
        Object obj = GetStaticAllocRoot();
        while (obj._r_next != null) {
            obj = obj._r_next;
        }
        return obj;
    }

    @SJC.Inline
    public static Object ObjectPointingTo(Object o) {
        Object obj = GetStaticAllocRoot();
        while (obj._r_next != null) {
            if (obj._r_next == o) {
                return obj;
            }
            obj = obj._r_next;
        }
        return null;
    }
}
