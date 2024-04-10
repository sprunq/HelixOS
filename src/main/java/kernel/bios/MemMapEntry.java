package kernel.bios;

public class MemMapEntry {
    public final long base;
    public final long length;
    public final int type;

    public MemMapEntry(long base, long length, int type) {
        this.base = base;
        this.length = length;
        this.type = type;
    }
}
