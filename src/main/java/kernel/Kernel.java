package kernel;

import kernel.display.textmode.TmWriter;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        prelude();
        main_code();
    }

    private static void prelude() {
        MemoryManager.init();
        out = new TmWriter();
    }

    private static void main_code() {
        out.clearScreen();

        TestAllocC a = new TestAllocC();
        TestAllocC b = new TestAllocC();
        TestAllocC c = new TestAllocC();
        TestAllocC d = new TestAllocC();

        int firstAddr = MemoryManager.getFirstAdr();
        Object obj = MAGIC.cast2Obj(firstAddr);
        while (obj != null) {
            out.print("object(relocEntries=");
            out.print(obj._r_relocEntries);
            out.print(", scalarSize=");
            out.print(obj._r_scalarSize);
            out.println(")");

            obj = obj._r_next;
            sleep();
        }

        while (true) {
        }
    }

    private static void sleep() {
        for (int i = 0; i < 100000000; i++) {
        }
    }
}
