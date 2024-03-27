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
        Kernel.out.print("TestAllocA(");
        Kernel.out.print(a, 10);
        Kernel.out.print(", ");
        Kernel.out.print(b, 10);
        Kernel.out.print(", ");
        Kernel.out.print(c, 10);
        Kernel.out.print(", ");
        Kernel.out.print(bool);
        Kernel.out.print(", ");
        Kernel.out.print(str);
        Kernel.out.print(")");
    }
}
