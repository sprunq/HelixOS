package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter out;

    public static void main() {
        out = new TmWriter();
        out.clearScreen();
        show_alloc_functionality();
        out.println();
        test_memory_limit();
    }

    private static void show_alloc_functionality() {
        TestAllocA a = new TestAllocA(5, 7, 9, true, "hello from a");
        TestAllocB b = new TestAllocB(a);

        // Look for these objects in the heap
        // If they are found, they will be highlighted in green
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
            out.print(obj._r_relocEntries);
            out.print(", scalarSize=");
            out.print(obj._r_scalarSize);
            out.println(")");
            obj = obj._r_next;
            foundObjects++;
        }

        out.brush.setFg(TmColor.WHITE);
        out.print("Found ");
        out.print(foundObjects);
        out.println(" objects.");

        out.print("Consumed memory: ");
        out.print(MemoryManager.getConsumedMemory());
        out.println("b");

        // Show that the objects are not overwritten
        TestAllocA c = new TestAllocA(11, 13, 15, false, "hello from c");
        TestAllocB d = new TestAllocB(c);

        b.print();
        out.println();
        d.print();

        out.println();
    }

    private static void test_memory_limit() {
        out.println("Testing memory limit. Allocating until failure...");

        int i = 0;
        while (true) {
            byte[] b = new byte[1024];

            if (i % 100000 == 0) {
                out.print("Allocation #");
                out.print(i);
                out.print(" at ADR ");
                out.println(MAGIC.cast2Ref(b));
            }
            i++;
        }
    }

    public static void panic(String msg) {
        final byte colBorder = TmColor.set(TmColor.BLACK, TmColor.RED);
        final byte colTextMsg = TmColor.set(TmColor.LIGHT_RED, TmColor.BLACK);
        final byte colTextPanic = TmColor.set(TmColor.RED, TmColor.BLACK);
        final byte clearCol = TmColor.set(TmColor.GREY, TmColor.BLACK);

        TmWriter.setLine(0, (byte) ' ', clearCol);
        TmWriter.setLine(1, (byte) ' ', clearCol);
        TmWriter.setLine(2, (byte) ' ', clearCol);

        int pos = 0;
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.directPrint(" PANIC: ", pos, colTextPanic);
        pos = TmWriter.directPrint(msg, pos, colTextMsg);
        pos = TmWriter.newLinePos(pos);
        pos = TmWriter.directPrint(' ', pos, colBorder);
        pos = TmWriter.newLinePos(pos);
        while (true) {
        }
    }
}
