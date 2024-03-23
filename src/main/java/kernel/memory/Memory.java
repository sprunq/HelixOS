package kernel.memory;

public class Memory {
    @SJC.Inline
    public static void setBytes(int addr, int len, byte value) {
        while (len-- > 0) {
            MAGIC.wMem8(addr++, value);
        }
    }

    public static void copyBytes(int src, int dest, int len) {
        while (len-- > 0) {
            MAGIC.wMem8(dest++, MAGIC.rMem8(src++));
        }
    }
}
