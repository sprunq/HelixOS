package kernel;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;
import kernel.memory.MemoryManager;

public class Kernel {
    public static TmWriter Display;

    public static void main() {
        MemoryManager.init();
        Kernel.Display = new TmWriter();
        Display.clearScreen();
        show_alloc_functionality();
        Display.println();
        test_memory_limit();
    }

    private static void show_alloc_functionality() {
        TestAllocA a = new TestAllocA(5, 7, 9, true, "hello from a");
        TestAllocB b = new TestAllocB(a);

        int[] addrs = new int[20];
        addrs[0] = MAGIC.cast2Ref(a);

        Object obj = MemoryManager.getDynamicAllocRoot();
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
                Display.brush.setFg(TmColor.LIGHT_GREEN);
            } else {
                Display.brush.setFg(TmColor.LIGHT_RED);
            }

            Display.print("0x");
            Display.print(addr, 16);
            Display.print(": ");
            Display.print("object(relocEntries=");
            Display.print(obj._r_relocEntries);
            Display.print(", scalarSize=");
            Display.print(obj._r_scalarSize);
            Display.println(")");
            obj = obj._r_next;
            foundObjects++;
        }

        Display.brush.setFg(TmColor.WHITE);
        Display.print("Found ");
        Display.print(foundObjects);
        Display.println(" objects.");

        Display.print("Consumed memory: ");
        Display.print(getDynamicAllocationSize());
        Display.println("b");

        // Show that the objects are not overwritten
        TestAllocA c = new TestAllocA(11, 13, 15, false, "hello from c");
        TestAllocB d = new TestAllocB(c);

        b.print();
        Display.println();
        d.print();

        Display.println();
    }

    private static void test_memory_limit() {
        Display.println("Testing memory limit. Allocating until failure...");

        int i = 0;
        while (true) {
            byte[] b = new byte[1024];

            if (i % 100000 == 0) {
                Display.print("Allocation #");
                Display.print(i);
                Display.print(" at ADR ");
                Display.println(MAGIC.cast2Ref(b));
            }
            i++;
        }
    }

    private static int getDynamicAllocationSize() {
        int consumed = 0;
        Object obj = MemoryManager.getDynamicAllocRoot();
        while (obj != null) {
            consumed += obj._r_scalarSize;
            obj = obj._r_next;
        }
        return consumed;
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
