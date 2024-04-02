package kernel;

public class TestAllocB {
    public TestAllocA a;

    public TestAllocB(TestAllocA a) {
        this.a = a;
    }

    public void print() {
        Kernel.out.print("TestAllocB(");
        this.a.print();
        Kernel.out.print(")");
    }
}
