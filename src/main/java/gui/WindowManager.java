package gui;

import kernel.display.GraphicsContext;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import kernel.schedeule.Task;
import kernel.trace.logging.Logger;
import util.vector.VecWidget;

public class WindowManager extends Task implements IKeyboardEventListener {
    private GraphicsContext _ctx;
    private VecWidget _windows;
    private Widget _selectedWindow;

    public WindowManager(GraphicsContext ctx) {
        super("_task_win_WindowManager");
        _windows = new VecWidget();
        this._ctx = ctx;
    }

    public void AddWindow(Widget window) {
        _windows.add(window);
        _windows.SortByZ();
    }

    public void NextSlection() {
        if (_selectedWindow == null) {
            _selectedWindow = _windows.MaxSelectable();
        } else {
            _selectedWindow.SetSelected(false);
            _selectedWindow = _windows.NextSelectable(_selectedWindow);
        }
        if (_selectedWindow == null) {
            Logger.Info("WIN", "No selectable widget found");
            return;
        }
        _selectedWindow.SetSelected(true);
        Logger.Info("WIN", "Selected Widget ".append(_selectedWindow.Name));
    }

    public void DrawWindows() {
        if (_ctx == null) {
            return;
        }

        for (int i = 0; i < _windows.size(); i++) {
            Widget window = _windows.get(i);

            if (window == null) {
                continue;
            }

            if (window.NeedsRedraw()) {
                window.Draw(_ctx);
            }
        }

        _ctx.Swap();
    }

    public void StaticDisplayFor(int ms) {
        if (ms == 0) {
            return;
        }
        DrawWindows();
        _ctx.Swap();
        Timer.Sleep(ms);
        _ctx.ClearScreen();
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

    private boolean _ctrlDown = false;

    @Override
    public boolean OnKeyPressed(char keyCode) {
        switch ((int) keyCode) {
            case Key.LCTRL:
                _ctrlDown = true;
                return true;
            case Key.TAB:
                if (_ctrlDown) {
                    NextSlection();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean OnKeyReleased(char keyCode) {
        switch ((int) keyCode) {
            case Key.LCTRL:
                _ctrlDown = false;
                return true;
            default:
                return false;
        }
    }

    @Override
    public int Priority() {
        return 999;
    }
}
