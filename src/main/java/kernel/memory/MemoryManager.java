package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import rte.SClassDesc;
import util.BitHelper;

/**
 * The MemoryManager class is responsible for managing the memory in the system.
 * It handles both static and dynamic allocations.
 * Static allocations are done at compile time and stored in the boot image.
 * Dynamic allocations are done at runtime and stored in the dynamic heap.
 * Dynamic allocations are stored in a sub list from the static allocations, so
 * that Garbage Collection can be done on the dynamic heap without affecting the
 * static heap.
 */
public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    /*
     * The root of the dynamic allocation. This object should never be freed.
     */
    private static Object _dynamicAllocRoot = null;

    /*
     * The address of the last allocation in the dynamic heap.
     * Cached to avoid traversing the dynamic heap every time an allocation is done.
     */
    private static int _lastAllocation = -1;

    /*
     * Initializes the memory manager. This method is called once at the start of
     * the system and has to be called before any dynamic allocations are done.
     */
    public static void initialize() {
        int dynHeapStart = getDynamicHeapStart();
        int rootScalarSize = MAGIC.getInstScalarSize("DynamicAllocRoot");
        int rootRelocsEntries = MAGIC.getInstRelocEntries("DynamicAllocRoot");
        SClassDesc rootType = (SClassDesc) MAGIC.clssDesc("DynamicAllocRoot");
        _dynamicAllocRoot = writeObject(dynHeapStart, rootScalarSize, rootRelocsEntries, rootType);

        // Link the dynamicAllocRoot to the last static object
        Object lastStaticObj = getStaticAllocLast();
        MAGIC.assign(lastStaticObj._r_next, _dynamicAllocRoot);
    }

    /**
     * Allocates an object in the dynamic heap.
     * 
     * @param scalarSize   The size of the scalar fields in the object.
     * @param relocEntries The number of relocation entries in the object.
     * @param type         The type of the object.
     * @return The allocated object.
     */
    public static Object allocObject(int scalarSize, int relocEntries, SClassDesc type) {
        // Get the last object in the dynamic heap
        Object lastAlloc = getLastDynamicHeapObj();
        int lastAllocAddr = MAGIC.cast2Ref(lastAlloc);

        // Align the start of the next object to 4 bytes
        int lastAllocEnd = lastAllocAddr + lastAlloc._r_scalarSize;
        int ptrNextFree = BitHelper.align(lastAllocEnd, 4);

        // Write the object to memory
        Object obj = writeObject(ptrNextFree, scalarSize, relocEntries, type);

        // Link the object into the chain
        MAGIC.assign(lastAlloc._r_next, obj);

        // Update the last allocation
        _lastAllocation = MAGIC.cast2Ref(obj);
        return obj;
    }

    /*
     * Returns the root of the dynamic allocation.
     * The dynamic allocation is a sub list of the list returned by
     * getStaticAllocRoot.
     */
    @SJC.Inline
    public static Object getDynamicAllocRoot() {
        return _dynamicAllocRoot;
    }

    /*
     * Returns the first object in the static allocation.
     */
    @SJC.Inline
    public static Object getStaticAllocRoot() {
        return MAGIC.cast2Obj(BOOT_IMAGE.firstHeapObject);
    }

    /**
     * Returns the last object in the static allocation.
     */
    public static Object getStaticAllocLast() {
        int addrDynamicRoot = MAGIC.cast2Ref(_dynamicAllocRoot);
        Object obj = getStaticAllocRoot();
        while (obj._r_next != null && MAGIC.cast2Ref(obj) != addrDynamicRoot) {
            obj = obj._r_next;
        }
        return obj;
    }

    /**
     * Returns the last object in the dynamic allocation.
     */
    public static Object getLastDynamicHeapObj() {
        if (_lastAllocation <= 0) {
            Object obj = getDynamicAllocRoot();
            while (obj._r_next != null) {
                obj = obj._r_next;
            }
            _lastAllocation = MAGIC.cast2Ref(obj);
        }
        return MAGIC.cast2Obj(_lastAllocation);
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
    private static Object writeObject(int ptrNextFree, int scalarSize, int relocEntries, SClassDesc type) {
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
        Memory.memset(startOfObject, lengthOfObject, (byte) 0);

        // cast2Obj expects the pointer to the first scalar field.
        // It needs space because relocs will be stored in front of the object
        int firstScalarField = startOfObject + relocsSize;

        Object obj = MAGIC.cast2Obj(firstScalarField);
        MAGIC.assign(obj._r_type, type);
        MAGIC.assign(obj._r_scalarSize, scalarSize);
        MAGIC.assign(obj._r_relocEntries, relocEntries);

        allocationChunk += lengthOfObject;
        if (allocationChunk > 64 * 1024) {
            allocationChunk = 0;
            // Logger.warning("Alloc 64kb - no GC /('o.o)\\");
        }

        return obj;
    }

    private static int allocationChunk = 0;

    @SJC.Inline
    private static int getDynamicHeapStart() {
        int adr = BOOT_IMAGE.memoryStart + BOOT_IMAGE.memorySize;
        return BitHelper.align(adr, 4);
    }
}
