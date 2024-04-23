package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.bios.call.MemMap;
import kernel.display.text.TM3;
import kernel.display.text.TM3Color;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    public static EmptyObject _firstEmptyObject = null;

    public static void initialize() {
        initEmptyObjects();
    }

    private static void initEmptyObjects() {
        Object lastStaticObj = getStaticAllocLast();
        int lastStaticObjAddr = MAGIC.cast2Ref(lastStaticObj);
        int continuationIndex = 0;
        do {
            MemMap.execMemMap(continuationIndex);
            continuationIndex = MemMap.getMemMapContinuationIndex();
            boolean isFree = MemMap.memMapTypeIsFree();
            long base = MemMap.getMemMapBase();
            long length = MemMap.getMemMapLength();

            if (!isFree
                    || base < MemoryLayout.BIOS_STKEND
                    || length <= MAGIC.getInstScalarSize("EmptyObject")
                            + MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize) {
                continue;
            }

            boolean segmentContainsStaticObjects = base <= lastStaticObjAddr && lastStaticObjAddr <= base + length - 1;
            if (!segmentContainsStaticObjects) {
                Kernel.panic("unimplemented");
            } else {
                int emptyLength = ((int) length) - EmptyObject.BaseSize();
                int ptrNextFree = getPtrNextFree(lastStaticObj);
                Object emptyObj = writeObject(ptrNextFree,
                        BitHelper.alignDown(emptyLength - lastStaticObjAddr - 1, 4),
                        EmptyObject.RelocEntries(),
                        EmptyObject.Type());

                MAGIC.assign(lastStaticObj._r_next, emptyObj);

                if (_firstEmptyObject == null) {
                    _firstEmptyObject = (EmptyObject) emptyObj;
                } else {
                    EmptyObject t = _firstEmptyObject;
                    while (t.nextEmptyObject != null) {
                        t = t.nextEmptyObject;
                    }
                    t.nextEmptyObject = (EmptyObject) emptyObj;
                    ((EmptyObject) emptyObj).prevEmptyObject = t;
                }
            }

        } while (continuationIndex != 0);
    }

    public static Object allocObject(int scalarSize, int relocEntries, SClassDesc type) {
        int alignScalarSize = BitHelper.align(scalarSize, 4);
        int newObjectTotalSize = alignScalarSize + relocEntries * MAGIC.ptrSize;

        EmptyObject emptyObj = findEmptyObjectFitting(newObjectTotalSize);
        if (emptyObj == null) {
            Kernel.panic("Out of memory");
        }

        if (objectSize(emptyObj) == newObjectTotalSize) {
            Kernel.panic("perfect fit not implemented");
        }

        int emptyObjectTop = emptyObj.Top();
        int newObjectBottom = emptyObjectTop - newObjectTotalSize;
        // TM3.sprint(newObjectTotalSize, 10, 240, TM3Color.VIOLET);

        Object newObject = writeObject(newObjectBottom, scalarSize, relocEntries, type);

        MAGIC.assign(emptyObj._r_scalarSize, emptyObj._r_scalarSize - newObjectTotalSize);

        return newObject;
    }

    private static int objectSize(Object o) {
        return o._r_scalarSize + o._r_relocEntries * MAGIC.ptrSize;
    }

    private static int objectSize(int scalarSize, int relocEntries) {
        int alignScalarSize = BitHelper.align(scalarSize, 4);
        return alignScalarSize + relocEntries * MAGIC.ptrSize;
    }

    public static void shrinkEmptyObject(EmptyObject obj, int shrinkBy) {
        int newEmptyObjSize = obj.Size() - shrinkBy;
        MAGIC.assign(obj._r_scalarSize, newEmptyObjSize);
    }

    private static EmptyObject findEmptyObjectFitting(int objectSize) {
        EmptyObject emptyObj = _firstEmptyObject;
        while (emptyObj != null) {
            if (emptyObj.fits(objectSize)) {
                break;
            }
            emptyObj = emptyObj.nextEmptyObject;
        }
        return emptyObj;
    }

    private static int getPtrNextFree(Object lastAlloc) {
        int lastAllocAddr = MAGIC.cast2Ref(lastAlloc);
        int lastAllocEnd = lastAllocAddr + lastAlloc._r_scalarSize;
        int ptrNextFree = BitHelper.align(lastAllocEnd, 4);
        return ptrNextFree;
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
    @SJC.Inline
    public static Object getStaticAllocLast() {
        Object obj = getStaticAllocRoot();
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

        return obj;
    }

    @SJC.Inline
    public static int getDynamicHeapStart() {
        int adr = BOOT_IMAGE.memoryStart + BOOT_IMAGE.memorySize;
        return BitHelper.align(adr, 4);
    }
}
