package kernel.memory;

public class Memory {
    @SJC.Inline
    public static void setBytes(int addr, int len, byte value) {
        int end = addr + len;
        if (len % 8 == 0) {
            for (int i = addr; i < end; i += 8) {
                MAGIC.wMem64(i, (long) value);
            }
        } else if (len % 4 == 0) {
            for (int i = addr; i < end; i += 4) {
                MAGIC.wMem32(i, (int) value);
            }
        } else if (len % 2 == 0) {
            for (int i = addr; i < end; i += 2) {
                MAGIC.wMem16(i, (short) value);
            }
        } else {
            for (int i = addr; i < end; i++) {
                MAGIC.wMem8(i, value);
            }
        }
    }

    @SJC.Inline
    public static void copyBytes(int src, int dest, int len) {
        while (len-- > 0) {
            MAGIC.wMem8(dest++, MAGIC.rMem8(src++));
        }
    }
}
