package kernel;

public class TestAllocA {
    int a, b, c;
    boolean bool;
    String str;

    public TestAllocA(int a, int b, int c, boolean bool, String str) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.bool = bool;
        this.str = str;
    }

    public void print() {
        Kernel.Display.print("TestAllocA(");
        Kernel.Display.print(a, 10);
        Kernel.Display.print(", ");
        Kernel.Display.print(b, 10);
        Kernel.Display.print(", ");
        Kernel.Display.print(c, 10);
        Kernel.Display.print(", ");
        Kernel.Display.print(bool);
        Kernel.Display.print(", ");
        Kernel.Display.print(str);
        Kernel.Display.print(")");
    }
}
