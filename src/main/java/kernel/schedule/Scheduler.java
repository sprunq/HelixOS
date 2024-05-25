package kernel.schedule;

import arch.x86;
import kernel.Kernel;
import kernel.memory.MemoryManager;
import kernel.trace.logging.Logger;

public class Scheduler {
    public static final int MAX_TASKS = 32;
    private static Task[] _tasks;
    private static int _taskCount;
    private static Task _currentTask;

    public static void Initialize() {
        _tasks = new Task[MAX_TASKS];
        _taskCount = 0;
        _currentTask = null;
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

    public static int GetTaskId(String name) {
        for (int i = 0; i < _taskCount; i++) {
            if (_tasks[i].Name == name) {
                return _tasks[i].Id;
            }
        }
        return -1;
    }

    public static void Run() {
        Logger.Info("SCHED", "Starting Schedeuler");

        MemoryManager.EnableGarbageCollection();
        Logger.Info("SCHED", "Enabled Garbage Collection");

        // Ablage der Registerwerte in Variablen
        MAGIC.inline(0x89, 0x2D);
        MAGIC.inlineOffset(4, _v1); // mov [addr(v1)],ebp
        MAGIC.inline(0x89, 0x25);
        MAGIC.inlineOffset(4, _v2); // mov [addr(v1)],esp

        while (true) {
            for (int i = 0; i < _taskCount; i++) {
                _currentTask = _tasks[i];
                _currentTask.Run();

                if (MemoryManager.ShouldCollectGarbage()) {
                    MemoryManager.TriggerGarbageCollection();
                }
            }

            // x86.hlt();
        }
    }

    private static int _v1, _v2; // Variante mit Hilfe von Variablen

    public static void TaskBreak() {
        if (_currentTask == null) {
            return;
        }

        RemoveTask(_currentTask);
        _currentTask = null;

        x86.sti();

        // Beschreiben der Register aus gespeicherten Variablenwerten
        MAGIC.inline(0x8B, 0x2D);
        MAGIC.inlineOffset(4, _v1); // mov ebp,[addr(v1)]
        MAGIC.inline(0x8B, 0x25);
        MAGIC.inlineOffset(4, _v2); // mov esp,[addr(v1)]
    }
}
