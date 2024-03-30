package kernel;

public class TestAllocB {
    public TestAllocA a;

    public TestAllocB(TestAllocA a) {
        this.a = a;
    }

    public void print() {
        Kernel.Display.print("TestAllocB(");
        this.a.print();
        Kernel.Display.print(")");
    }
}
