package kernel.memory;

public class Memory {
    public static void memset(int start, int len, byte value) {
        int end = start + len;
        if (len % 4 == 0) {
            memset32(start, len / 4, value);
        } else {
            for (int i = start; i < end; i++) {
                MAGIC.wMem8(i, value);
            }
        }
    }

    @SJC.NoInline
    public static void memset32(int start, int len, int value) {
        MAGIC.inlineBlock("memset32");
    }

    /*
     * Copy bytes from one memory location to another.
     */
    public static void memcopy(int from, int to, int len) {
        if (len % 4 == 0) {
            memcopy32(from, to, len / 4);
        } else {
            while (len > 0) {
                MAGIC.wMem8(to, MAGIC.rMem8(from));
                from++;
                to++;
                len--;
            }
        }
    }

    @SJC.NoInline
    private static void memcopy32(int from, int to, int cnt) {
        MAGIC.inlineBlock("memcopy32");
    }
}
