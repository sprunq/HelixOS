package kernel;

public class Env {
    /*
     * The memory limit is set to 1GB. This is the maximum amount of memory that
     * can be allocated by the kernel. If the kernel tries to allocate more memory
     * than this limit, it will panic.
     */
    public static final int MEMORY_LIMIT = 1024 * 1024 * 1024;

    /*
     * The memory address of the VGA text mode 3 buffer.
     */
    public static final int VGA_TM3_BUFFER = 0xB8000;
}
