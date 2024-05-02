package gui;

import kernel.display.ADisplay;
import kernel.hardware.Timer;
import kernel.schedeule.Task;
import util.vector.VecWindow;

public class WindowManager extends Task {
    private VecWindow _windows;
    public ADisplay _display;

    public WindowManager(ADisplay display) {
        _windows = new VecWindow();
        this._display = display;
    }

    public void AddWindow(ADisplayElement window) {
        _windows.add(window);
        _windows.SortByZ();
    }

    public void DrawWindows() {

        if (_display == null) {
            return;
        }

        for (int i = 0; i < _windows.size(); i++) {
            ADisplayElement window = _windows.get(i);

            if (window == null) {
                continue;
            }

            if (window.NeedsRedraw()) {
                window.Draw(_display);
            }
        }

        _display.Swap();
    }

    public void StaticDisplayFor(int ms) {
        if (ms == 0) {
            return;
        }
        DrawWindows();
        _display.Swap();
        Timer.Sleep(ms);
        _display.ClearScreen();
    }

    @Override
    public void Run() {
        DrawWindows();
        _lastExecTick = Timer.Ticks();
    }

    private int _lastExecTick = 0;

    @Override
    public boolean WantsActive() {
        int currentTick = Timer.Ticks();
        if (Timer.TickDifferenceMs(_lastExecTick, currentTick) > 1000 / 60) {
            return true;
        }
        return false;
    }
}
