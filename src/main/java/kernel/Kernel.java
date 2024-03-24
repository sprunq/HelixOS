package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.lib.NoAllocConv;
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

        TestAllocA a = new TestAllocA();
        TestAllocB b = new TestAllocB();
        TestAllocC c = new TestAllocC();

        int[] addrs = new int[100];
        addrs[0] = MAGIC.cast2Ref(a);
        addrs[1] = MAGIC.cast2Ref(b);
        addrs[2] = MAGIC.cast2Ref(c);
        addrs[3] = MAGIC.cast2Ref(addrs);
        addrs[4] = MAGIC.cast2Ref(out);
        addrs[5] = MAGIC.cast2Ref(out.brush);
        addrs[6] = MAGIC.cast2Ref(NoAllocConv.ALPHABET);

        int firstAddr = MemoryManager.getFirstAdr();
        Object obj = MAGIC.cast2Obj(firstAddr + 8);
        int foundObjects = 0;
        while (obj != null) {
            int addr = MAGIC.cast2Ref(obj);

            boolean found = false;
            for (int i = 0; i < addrs.length; i++) {
                if (addrs[i] == addr) {
                    found = true;
                    break;
                }
            }

            if (found) {
                out.brush.setFg(TmColor.LIGHT_GREEN);
            } else {
                out.brush.setFg(TmColor.LIGHT_RED);
            }

            out.print("0x");
            out.print(addr, 16);
            out.print(": ");
            out.print("object(relocEntries=");
            out.print(obj._r_relocEntries, 10);
            out.print(", scalarSize=");
            out.print(obj._r_scalarSize, 10);
            out.println(")");
            obj = obj._r_next;
            foundObjects++;
            sleep();
        }

        out.brush.setFg(TmColor.WHITE);

        out.println("Finished rendering objects.");
        out.print("Found ");
        out.print(foundObjects, 10);
        out.println(" objects.");

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
