package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        out = new TmWriter();
        show_alloc_functionality();
        test_memory_limit();
    }

    private static void test_memory_limit() {
        out.clearScreen();
        int i = 0;
        while (true) {
            i++;
            byte[] b = new byte[500];

            if (i % 100000000 == 0) {
                out.println(MAGIC.cast2Ref(b), 10);
            }
        }

        while (true) {
        }

    }

    private static void show_alloc_functionality() {
        out.clearScreen();

        TestAllocA a = new TestAllocA(5, 7, 9, true, "hello from a");
        TestAllocB b = new TestAllocB(a);

        int[] addrs = new int[6];
        addrs[0] = MAGIC.cast2Ref(a);
        addrs[1] = MAGIC.cast2Ref(b);
        addrs[3] = MAGIC.cast2Ref(addrs);
        addrs[4] = MAGIC.cast2Ref(out);
        addrs[5] = MAGIC.cast2Ref(out.brush);

        Object obj = MemoryManager.getFirstHeapObj();
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
        }

        out.brush.setFg(TmColor.WHITE);
        out.print("Found ");
        out.print(foundObjects, 10);
        out.println(" objects.");

        out.print("Consumed memory: ");
        out.print(MemoryManager.getConsumedMemory(), 10);
        out.println("b");

        // Show that the objects are not overwritten
        TestAllocA c = new TestAllocA(11, 13, 15, false, "hello from c");
        TestAllocB d = new TestAllocB(c);

        b.print();
        out.println();
        d.print();

        out.println();

        while (true) {
        }
    }

    public static void panic(String msg) {
        byte border = TmColor.set(TmColor.BLACK, TmColor.RED);
        byte textMsg = TmColor.set(TmColor.LIGHT_RED, TmColor.BLACK);
        byte textPanic = TmColor.set(TmColor.RED, TmColor.BLACK);
        int pos = 0;
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.directPrint(" PANIC: ", pos, textPanic);
        pos = TmWriter.directPrint(msg, pos, textMsg);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, border);
        pos = TmWriter.newLinePos(pos);
        while (true) {
        }
    }
}