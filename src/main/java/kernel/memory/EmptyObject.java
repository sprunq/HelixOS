package kernel.memory;

import rte.SClassDesc;

public class EmptyObject extends Object {
    public EmptyObject nextEmptyObject;
    public EmptyObject prevEmptyObject;

    public int Top() {
        return MAGIC.cast2Ref(this) + _r_scalarSize;
    }

    public int Size() {
        return _r_scalarSize + _r_relocEntries * MAGIC.ptrSize;
    }

    public static int BaseSize() {
        return MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize + MAGIC.getInstScalarSize("EmptyObject");
    }

    public static int RelocEntries() {
        return MAGIC.getInstRelocEntries("EmptyObject");
    }

    public static SClassDesc Type() {
        return (SClassDesc) MAGIC.clssDesc("EmptyObject");
    }

    public boolean fits(int size) {
        return _r_scalarSize >= size;
    }
}
