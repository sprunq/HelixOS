package kernel.schedeule;

import kernel.Kernel;
import kernel.memory.MemoryManager;

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
        MemoryManager.EnableGarbageCollection();
        while (true) {
            for (int i = 0; i < _taskCount; i++) {
                if (_tasks[i].WantsActive()) {
                    _tasks[i].Run();
                }
            }

            if (MemoryManager.ShouldCollectGarbage()) {
                MemoryManager.TriggerGarbageCollection();
            }
        }
    }

    public static int GetTaskId(String name) {
        for (int i = 0; i < _taskCount; i++) {
            if (_tasks[i].Name == name) {
                return _tasks[i].Id;
            }
        }
        return -1;
    }
}
