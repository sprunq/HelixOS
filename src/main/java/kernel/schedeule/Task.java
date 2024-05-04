package kernel.schedeule;

public abstract class Task {
    public final int Id;
    public final String Name;
    protected boolean _active;
    protected boolean _running;

    public Task(String name) {
        Id = NextId();
        Name = name;
        _active = false;
        _running = false;

        Schedeuler.AddTask(this);
    }

    public abstract boolean WantsActive();

    public final void RunTask() {
        _active = true;
        Run();
        _active = false;
    }

    public abstract void Run();

    private static int _idC = 0;

    protected static int NextId() {
        return _idC++;
    }
}
