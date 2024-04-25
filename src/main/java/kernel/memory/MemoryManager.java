package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.bios.call.MemMap;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    /*
     * The DynamicAllocRoot marks the beginning of the dynamic allocation.
     * It is allocated as the first object after the static allocation,
     * and marks the beginning of the dynamic allocations.
     */
    private static DynamicAllocRoot _dynamicAllocRoot;

    /*
     * The EmptyObjectRoot marks the beginning of the empty object chain.
     * Empty objects are used to keep track of free memory regions.
     */
    private static EmptyObject _emptyObjectRoot;

    /**
     * The last allocation address used by the memory manager.
     * This variable keeps track of the last memory address that was allocated.
     * Should only be accessed through the LastAlloc() and SetLastAlloc() methods.
     */
    private static int _lastAllocationAddress = -1;

    public static void Initialize() {
        InitEmptyObjects();
        if (_emptyObjectRoot == null) {
            Kernel.panic("No viable memory regions found");
        }

        _dynamicAllocRoot = (DynamicAllocRoot) allocObject(
                MAGIC.getInstScalarSize("DynamicAllocRoot"),
                MAGIC.getInstRelocEntries("DynamicAllocRoot"),
                (SClassDesc) MAGIC.clssDesc("DynamicAllocRoot"));
    }

    @SJC.Inline
    public static Object GetStaticAllocRoot() {
        return MAGIC.cast2Obj(BOOT_IMAGE.firstHeapObject);
    }

    @SJC.Inline
    public static DynamicAllocRoot GetDynamicAllocRoot() {
        return _dynamicAllocRoot;
    }

    @SJC.Inline
    public static EmptyObject GetEmptyObjectRoot() {
        return _emptyObjectRoot;
    }

    @SJC.Inline
    public static int ObjectSize(Object o) {
        return o._r_scalarSize + o._r_relocEntries * MAGIC.ptrSize;
    }

    /**
     * Invalidates the last allocation address.
     * Will be needed for garbage collection.
     */
    @SJC.Inline
    public static void InvalidateLastAlloc() {
        _lastAllocationAddress = -1;
    }

    /**
     * Returns the last allocated object.
     * If the last allocation address is not cached, it will be recalculated.
     * 
     * @return The last allocated object.
     */
    @SJC.Inline
    public static Object LastAlloc() {
        if (_lastAllocationAddress != -1) {
            return MAGIC.cast2Obj(_lastAllocationAddress);
        }

        // Recalculate the last allocation address
        // DynamicAllocRoot is null when we create the root object
        Object obj = _dynamicAllocRoot != null ? _dynamicAllocRoot : GetStaticAllocRoot();
        while (obj._r_next != null) {
            obj = obj._r_next;
        }
        _lastAllocationAddress = MAGIC.cast2Ref(obj);
        return obj;
    }

    /**
     * Sets the last allocated object cache value.
     */
    @SJC.Inline
    public static void SetLastAlloc(Object o) {
        if (o != null) {
            _lastAllocationAddress = MAGIC.cast2Ref(o);
        } else {
            InvalidateLastAlloc();
        }
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
            // The empty object will be overwritten
            int newObjectBottom = emptyObj.AddressBottom();
            newObject = WriteObject(newObjectBottom, scalarSize, relocEntries, type, true);
        } else if (emptyObj.UnreservedScalarSize() >= newObjectTotalSize) {
            // The new object does not fit exactly into the empty object
            // We need to split the empty object
            int newObjectBottom = emptyObj.AddressTop() - newObjectTotalSize;
            newObject = WriteObject(newObjectBottom, scalarSize, relocEntries, type, true);
            emptyObj.ShrinkBy(newObjectTotalSize);
        }

        if (newObject == null) {
            Kernel.panic("Failed to allocate object");
        }

        InsertIntoNextChain(LastAlloc(), newObject);
        SetLastAlloc(newObject);
        return newObject;
    }

    private static void InitEmptyObjects() {
        int contIndex = 0;
        do {
            MemMap.ExecMemMap(contIndex);
            contIndex = MemMap.GetMemMapContinuationIndex();
            boolean isFree = MemMap.MemMapTypeIsFree();
            long base = MemMap.GetMemMapBase();
            long length = MemMap.GetMemMapLength();
            long end = base + length;

            if (base < BOOT_IMAGE.memoryStart + BOOT_IMAGE.memorySize) {
                base = BOOT_IMAGE.memoryStart + BOOT_IMAGE.memorySize + 1;
            }

            if (!isFree
                    || base >= end
                    || length <= EmptyObject.MinimumClassSize()) {
                continue;
            }

            int emptyObjStart = (int) BitHelper.Align(base, 4);
            int emptyObjEnd = (int) BitHelper.AlignDown(end, 4);
            int emptyObjScalarSize = emptyObjEnd - emptyObjStart - EmptyObject.RelocEntriesSize();

            EmptyObject eo = (EmptyObject) WriteObject(
                    emptyObjStart,
                    emptyObjScalarSize,
                    EmptyObject.RelocEntries(),
                    EmptyObject.Type(),
                    true);
            InsertIntoEmptyObjectChain(eo);
        } while (contIndex != 0);
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
    private static Object WriteObject(int ptrNextFree, int scalarSize, int relocEntries, SClassDesc type,
            boolean clearMemory) {
        // Each reloc entry is a pointer
        int relocsSize = relocEntries * MAGIC.ptrSize;

        int startOfObject = ptrNextFree;
        int lengthOfObject = relocsSize + scalarSize;
        int endOfObject = startOfObject + lengthOfObject;

        // Check if the object fits into the memory. If not, panic
        if (endOfObject >= MemoryLayout.MEMORY_LIMIT) {
            Kernel.panic("Out of memory");
        }

        if (clearMemory) {
            Memory.Memset(startOfObject, lengthOfObject, (byte) 0);
        }

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
    private static void InsertIntoNextChain(Object insertAfter, Object o) {
        MAGIC.assign(o._r_next, insertAfter._r_next);
        MAGIC.assign(insertAfter._r_next, o);
    }

    /*
     * Adds an empty object to the chain of empty objects.
     * The chain is sorted by the address of the empty objects.
     */
    @SJC.Inline
    private static void InsertIntoEmptyObjectChain(EmptyObject emptyObj) {
        if (_emptyObjectRoot == null) {
            _emptyObjectRoot = emptyObj;
        } else {
            int emptyObjAddr = MAGIC.cast2Ref(emptyObj);
            EmptyObject t = _emptyObjectRoot;
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
            _emptyObjectRoot = emptyObj.NextEmptyObject;
        }
        if (emptyObj.NextEmptyObject != null) {
            emptyObj.NextEmptyObject.PrevEmptyObject = emptyObj.PrevEmptyObject;
        }
    }

    @SJC.Inline
    private static EmptyObject FindEmptyObjectFitting(int objectSize) {
        EmptyObject emptyObj = _emptyObjectRoot;
        while (emptyObj != null) {
            if (emptyObj.UnreservedScalarSize() >= objectSize
                    || ObjectSize(emptyObj) == objectSize) {
                return emptyObj;
            }
            emptyObj = emptyObj.NextEmptyObject;
        }
        return null;
    }
}
