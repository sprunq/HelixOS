package kernel.schedeule;

public abstract class Task {
    protected boolean _active;
    protected boolean _running;

    public abstract boolean WantsActive();

    public final void RunTask() {
        _active = true;
        Run();
        _active = false;
    }

    public abstract void Run();

}
