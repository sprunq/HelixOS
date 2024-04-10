package kernel.memory;

public class Memory {
    public static void setBytes(int start, int len, byte value) {
        int end = start + len;
        if (len % 8 == 0) {
            for (int i = start; i < end; i += 8) {
                MAGIC.wMem64(i, (long) value);
            }
        } else if (len % 4 == 0) {
            for (int i = start; i < end; i += 4) {
                MAGIC.wMem32(i, (int) value);
            }
        } else if (len % 2 == 0) {
            for (int i = start; i < end; i += 2) {
                MAGIC.wMem16(i, (short) value);
            }
        } else {
            for (int i = start; i < end; i++) {
                MAGIC.wMem8(i, value);
            }
        }
    }

    /*
     * Copy bytes from one memory location to another.
     * Bigger batches are faster even on 32-bit systems.
     */
    public static void copyBytes(int from, int to, int len) {
        if (len % 8 == 0) {
            while (len > 0) {
                MAGIC.wMem64(to, MAGIC.rMem64(from));
                from += 8;
                to += 8;
                len -= 8;
            }
        } else if (len % 4 == 0) {
            while (len > 0) {
                MAGIC.wMem32(to, MAGIC.rMem32(from));
                from += 4;
                to += 4;
                len -= 4;
            }
        } else if (len % 2 == 0) {
            while (len > 0) {
                MAGIC.wMem16(to, MAGIC.rMem16(from));
                from += 2;
                to += 2;
                len -= 2;
            }
        } else {
            while (len > 0) {
                MAGIC.wMem8(to, MAGIC.rMem8(from));
                from++;
                to++;
                len--;
            }
        }
    }
}
