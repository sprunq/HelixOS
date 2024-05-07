package gui.displays.windows;

import kernel.hardware.Timer;
import kernel.schedule.Task;

public class BounceTask extends Task {
    BounceWindow window;

    private int _tickRate = 1000 / 60;

    private int _lastTick = 0;

    public BounceTask(BounceWindow window) {
        super("_win_bounce_task");
        this.window = window;
    }

    @Override
    public boolean WantsActive() {
        int now = Timer.Ticks();
        return Timer.TicksToMs(now - _lastTick) >= _tickRate;
    }

    @Override
    public void Run() {
        window.UpdateBallPosition();
        _lastTick = Timer.Ticks();
    }
}
