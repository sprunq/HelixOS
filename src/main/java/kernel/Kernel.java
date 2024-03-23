package kernel;

import kernel.memory.MemoryManager;
import kernel.video.VidMem;
import kernel.video.OsWriter;

@SuppressWarnings("unused")
public class Kernel {
    private static final VidMem vidMem = (VidMem) MAGIC.cast2Struct(0xB8000);
    private static int vidPos;

    public static void main() {
        prelude();
        main_code();
    }

    private static void prelude() {
        MemoryManager.init();
    }

    private static void main_code() {
        OsWriter.clearScreen();

        TestAllocC c = new TestAllocC();

        OsWriter.println();

        Object obj = MemoryManager.getFirstObject();
        while (obj != null) {
            MemoryManager.renderObject(obj);
            obj = obj._r_next;
        }

        while (true) {
        }
    }
}
