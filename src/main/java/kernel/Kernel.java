package kernel;

import kernel.memory.MemoryManager;
import kernel.video.VidMem;
import kernel.video.TextWriter;

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
        TextWriter.clearScreen();

        TestAlloc a = new TestAlloc();

        for (int i = 0; i < a.c.length; i++) {
            TextWriter.print((byte) (a.c[i] + '0'));
            TextWriter.print(", ");

        }

        a.c[7] = 20;

        TextWriter.newline();
        for (int i = 0; i < a.c.length; i++) {
            TextWriter.print((int) a.c[i]);
            TextWriter.print(", ");

        }

        TextWriter.print("done");

        while (true) {
        }
    }
}
