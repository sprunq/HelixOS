package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.memory.BootableImage;
import kernel.memory.MemoryManager;
import kernel.memory.MemoryView;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        prelude();
        main_code();
    }

    private static void prelude() {
        MemoryManager.init();
        out = new TmWriter();
        out.clearScreen();
    }

    private static void main_code() {
        TestAllocC a = new TestAllocC();
        TestAllocC b = new TestAllocC();
        TestAllocC c = new TestAllocC();

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

        out.println("Finished rendering objects.");

        while (true) {
        }
    }

    private static void sleep() {
        for (int i = 0; i < 100000000; i++) {
        }
    }

    public static void panic(String msg) {
        int pos = 0;
        pos = TmWriter.directPrint(' ', pos, TmColor.set(TmColor.BLACK, TmColor.RED));
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, TmColor.set(TmColor.BLACK, TmColor.RED));
        pos = TmWriter.directPrint(" PANIC: ", pos, TmColor.RED);
        pos = TmWriter.directPrint(msg, pos, TmColor.set(TmColor.LIGHT_RED, TmColor.BLACK));
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, TmColor.set(TmColor.BLACK, TmColor.RED));
        pos = TmWriter.newLinePos(pos);
        while (true) {
        }
    }

}
