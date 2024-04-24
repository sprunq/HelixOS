package kernel.memory;

import rte.SClassDesc;

public class EmptyObject extends Object {
    /*
     * Points to the next empty object in the list or null if this is the last
     * empty object.
     */
    public EmptyObject NextEmptyObject;

    /*
     * Points to the previous empty object in the list or null if this is the
     * first empty object.
     */
    public EmptyObject PrevEmptyObject;

    @SJC.Inline
    public int AddressTop() {
        return MAGIC.cast2Ref(this) + _r_scalarSize;
    }

    @SJC.Inline
    public int AddressBottom() {
        return MAGIC.cast2Ref(this) - RelocEntriesSize();
    }

    @SJC.Inline
    public static int ClassSize() {
        return MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize + MAGIC.getInstScalarSize("EmptyObject");
    }

    @SJC.Inline
    public static int BaseScalarSize() {
        return MAGIC.getInstScalarSize("EmptyObject");
    }

    @SJC.Inline
    public static int RelocEntries() {
        return MAGIC.getInstRelocEntries("EmptyObject");
    }

    @SJC.Inline
    public static int RelocEntriesSize() {
        return MAGIC.getInstRelocEntries("EmptyObject") * MAGIC.ptrSize;
    }

    @SJC.Inline
    public static SClassDesc Type() {
        return (SClassDesc) MAGIC.clssDesc("EmptyObject");
    }

    @SJC.Inline
    public void ShrinkBy(int newObjectTotalSize) {
        MAGIC.assign(_r_scalarSize, _r_scalarSize - newObjectTotalSize);
    }

    @SJC.Inline
    public int ReservedSize() {
        return ReservedSize() + RelocEntriesSize();
    }

    @SJC.Inline
    public int UnreservedScalarSize() {
        return _r_scalarSize - MAGIC.getInstScalarSize("EmptyObject");
    }
}
