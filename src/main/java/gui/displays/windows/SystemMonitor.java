package gui.displays.windows;

import kernel.memory.GarbageCollector;
import kernel.schedeule.Task;
import util.queue.QueueInt;

public class SystemMonitor extends Task {

    private QueueInt _gcExecTimes;
    private QueueInt _gcObjectsCollected;
    private QueueInt _gcBytesCollected;
    private QueueInt _gcEmptyObjectsCompacted;
    private int _averageOver;

    public SystemMonitor() {
        super("_task_app_SystemMonitor");
        _averageOver = 10;
        _gcExecTimes = new QueueInt(_averageOver);
        _gcObjectsCollected = new QueueInt(_averageOver);
        _gcBytesCollected = new QueueInt(_averageOver);
        _gcEmptyObjectsCompacted = new QueueInt(_averageOver);
    }

    @Override
    public boolean WantsActive() {
        return true;
    }

    @Override
    public void Run() {
        int gcExecTime = GarbageCollector.InfoLastRunTimeMs;
        int gcObjectsCollected = GarbageCollector.InfoLastRunCollectedObjects;
        int gcBytesCollected = GarbageCollector.InfoLastRunCollectedBytes;
        int gcEmptyObjectsCompacted = GarbageCollector.InfoLastRunCompactedEmptyObjects;

        _gcExecTimes.Put(gcExecTime);
        _gcObjectsCollected.Put(gcObjectsCollected);
        _gcBytesCollected.Put(gcBytesCollected);
        _gcEmptyObjectsCompacted.Put(gcEmptyObjectsCompacted);
    }
}
