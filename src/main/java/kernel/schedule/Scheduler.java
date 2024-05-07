package kernel.schedule;

import kernel.Kernel;
import kernel.memory.MemoryManager;
import kernel.trace.logging.Logger;

public class Scheduler {
    public static final int MAX_TASKS = 20;
    private static Task[] _tasks;
    private static int _taskCount;

    public static void Initialize() {
        _tasks = new Task[MAX_TASKS];
    }

    public static void AddTask(Task task) {
        Logger.Info("SCHED", "Added Task ".append(task.Name));
        if (_taskCount >= MAX_TASKS) {
            Kernel.panic("Too many tasks");
            return;
        }
        _tasks[_taskCount++] = task;
    }

    public static void RemoveTask(Task task) {
        Logger.Info("SCHED", "Removing Task ".append(task.Name));
        for (int i = 0; i < _taskCount; i++) {
            if (_tasks[i] == task) {
                _tasks[i] = null;
                for (int j = i; j < _taskCount - 1; j++) {
                    _tasks[j] = _tasks[j + 1];
                }
                _taskCount--;
                return;
            }
        }
    }

    public static void Run() {
        Logger.Info("SCHED", "Starting Schedeuler");

        MemoryManager.EnableGarbageCollection();
        Logger.Info("SCHED", "Enabled Garbage Collection");

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
