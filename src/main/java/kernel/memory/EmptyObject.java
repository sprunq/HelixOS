package kernel.memory;

import rte.SClassDesc;

public class EmptyObject extends Object {
    public EmptyObject nextEmptyObject;
    public EmptyObject prevEmptyObject;

    public int AddressTop() {
        return MAGIC.cast2Ref(this) + _r_scalarSize;
    }

    public int AddressBottom() {
        return MAGIC.cast2Ref(this) - RelocEntriesSize();
    }

    public int ReservedSize() {
        return _r_scalarSize + _r_relocEntries * MAGIC.ptrSize;
    }

    public static int ClassSize() {
        return MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize + MAGIC.getInstScalarSize("EmptyObject");
    }

    public static int RelocEntries() {
        return MAGIC.getInstRelocEntries("EmptyObject");
    }

    public static int RelocEntriesSize() {
        return MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize;
    }

    public static SClassDesc Type() {
        return (SClassDesc) MAGIC.clssDesc("EmptyObject");
    }

    public boolean Fits(int size) {
        return _r_scalarSize >= size;
    }

    public boolean CanBeReplacedBy(int size) {
        return _r_scalarSize - size >= ClassSize();
    }

    public void ShrinkBy(int newObjectTotalSize) {
        MAGIC.assign(_r_scalarSize, _r_scalarSize - newObjectTotalSize);
    }
}
