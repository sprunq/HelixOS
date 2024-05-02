package kernel.schedeule;

import kernel.Kernel;
import kernel.memory.GarbageCollector;

public class Schedeuler {
    public static final int MAX_TASKS = 20;
    private static Task[] _tasks;
    private static int _taskCount;

    public static void Initialize() {
        _tasks = new Task[MAX_TASKS];
    }

    public static void AddTask(Task task) {
        if (_taskCount >= MAX_TASKS) {
            Kernel.panic("Too many tasks");
            return;
        }
        _tasks[_taskCount++] = task;
    }

    public static void Run() {
        int cycle = 0;
        while (true) {
            for (int i = 0; i < _taskCount; i++) {
                if (_tasks[i].WantsActive()) {
                    _tasks[i].Run();
                }
            }

            if (cycle % 10 == 0) {
                GarbageCollector.Run();
            }
            cycle++;
        }
    }
}
