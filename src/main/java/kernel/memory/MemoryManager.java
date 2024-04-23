package kernel.memory;

import kernel.Kernel;
import kernel.MemoryLayout;
import kernel.bios.call.MemMap;
import rte.SClassDesc;
import util.BitHelper;

public class MemoryManager {
    public static final BootableImage BOOT_IMAGE = (BootableImage) MAGIC.cast2Struct(MAGIC.imageBase);

    private static EmptyObject _firstEmptyObject = null;

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
                    || length < EmptyObject.BaseSize()) {
                continue;
            }

            boolean segmentContainsStaticObjects = base <= lastStaticObjAddr && lastStaticObjAddr <= base + length;
            if (!segmentContainsStaticObjects) {
                Kernel.panic("unimplemented");
            } else {
                int emptyLength = ((int) length) - EmptyObject.BaseSize();
                int ptrNextFree = getPtrNextFree(lastStaticObj);
                Object emptyObj = writeObject(ptrNextFree,
                        emptyLength,
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
        int newObjectSize = alignScalarSize + relocEntries * MAGIC.ptrSize;

        EmptyObject emptyObj = findEmptyObject(newObjectSize);
        if (emptyObj == null) {
            Kernel.panic("Out of memory");
        }

        int emptyObjAddr = MAGIC.cast2Ref(emptyObj);

        if (emptyObj.Size() == newObjectSize) {
            return writePerfectlyFittingObject(scalarSize, relocEntries, type, emptyObj, emptyObjAddr);
        }

        int emptyObjEnd = emptyObjAddr + emptyObj.Size();
        int newObjInEOAddr = BitHelper.align(emptyObjEnd - newObjectSize, 4);
        Object newObjInEO = writeObject(newObjInEOAddr, scalarSize, relocEntries, type);

        // Update the empty object
        int newEmptyObjSize = newObjInEOAddr - emptyObjAddr;
        MAGIC.assign(emptyObj._r_scalarSize, newEmptyObjSize);

        return newObjInEO;
    }

    private static Object writePerfectlyFittingObject(
            int scalarSize,
            int relocEntries,
            SClassDesc type,
            EmptyObject emptyObj,
            int emptyObjAddr) {
        if (emptyObj.prevEmptyObject != null) {
            emptyObj.prevEmptyObject.nextEmptyObject = emptyObj.nextEmptyObject;
        }
        if (emptyObj.nextEmptyObject != null) {
            emptyObj.nextEmptyObject.prevEmptyObject = emptyObj.prevEmptyObject;
        }
        if (_firstEmptyObject == emptyObj) {
            _firstEmptyObject = emptyObj.nextEmptyObject;
        }

        return writeObject(emptyObjAddr, scalarSize, relocEntries, type);
    }

    private static EmptyObject findEmptyObject(int objectSize) {
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
