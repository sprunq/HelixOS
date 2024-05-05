package gui;

import kernel.display.GraphicsContext;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.Key;
import kernel.hardware.keyboard.KeyEvent;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.mouse.MouseController;
import kernel.schedeule.Task;
import kernel.trace.logging.Logger;
import util.vector.VecWidget;

public class WindowManager extends Task {
    private GraphicsContext _ctx;
    private VecWidget _windows;
    private Widget _selectedWindow;
    private int _drawTicksAvgN = 50;
    private int _drawTicksAvgCycle = 0;
    private int _drawTicksAvgSum = 0;

    static public int InfoAvgRenderTimeMs = 0;

    public WindowManager(GraphicsContext ctx) {
        super("_win_window_manager");
        _windows = new VecWidget();
        this._ctx = ctx;
    }

    public void AddWindow(Widget window) {
        _windows.add(window);
        _windows.SortByZ();

        if (_selectedWindow == null && window.IsSelectable()) {
            _selectedWindow = window;
            _selectedWindow.SetSelected(true);
        }
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
        DistributeKeyEvents();
        DistributeMouseEvents();
        int start = Timer.Ticks();
        DrawWindows();
        DrawCursor();
        int end = Timer.Ticks();
        int renderTime = Timer.TicksToMs(end - start);
        _drawTicksAvgSum += renderTime;

        if (_drawTicksAvgCycle >= _drawTicksAvgN) {
            InfoAvgRenderTimeMs = _drawTicksAvgSum / _drawTicksAvgN;
            _drawTicksAvgSum = 0;
            _drawTicksAvgCycle = 0;
        }
        _drawTicksAvgCycle++;
    }

    public void DrawCursor() {
        if (_ctx == null) {
            return;
        }

        if (_lastMouseX >= 0 && _lastMouseX < _ctx.Width() && _lastMouseY >= 0 && _lastMouseY < _ctx.Height()) {
            _ctx.Rectangle(_lastMouseX, _lastMouseY, 10, 10, 0xFF);
        }
    }

    @Override
    public boolean WantsActive() {
        return true;
    }

    private void DistributeKeyEvents() {
        if (_selectedWindow == null) {
            return;
        }

        while (KeyboardController.HasNewEvent()) {
            if (KeyboardController.ReadEvent(_keyEvent)) {
                Logger.Trace("WIN", "Handling ".append(_keyEvent.Debug()));
                if (_keyEvent.IsDown) {
                    if (ConsumedInternalOnKeyPressed(_keyEvent.Key)) {
                        continue;
                    }

                    _selectedWindow.OnKeyPressed(_keyEvent.Key);
                } else {
                    if (ConsumedInternalOnKeyReleased(_keyEvent.Key)) {
                        continue;
                    }

                    _selectedWindow.OnKeyReleased(_keyEvent.Key);
                }
            }
        }
    }

    public void DistributeMouseEvents() {
        Logger.Trace("WIN", MouseController.Event.Debug());
        _lastMouseX += MouseController.Event.X_Delta;
        _lastMouseY -= MouseController.Event.Y_Delta;
    }

    private boolean _ctrlDown = false;
    private KeyEvent _keyEvent = new KeyEvent();
    private int _lastMouseX = 400;
    private int _lastMouseY = 400;

    private boolean ConsumedInternalOnKeyPressed(char keyCode) {
        switch (keyCode) {
            case Key.LCTRL:
                _ctrlDown = true;
                return true;
            case Key.TAB:
                if (_ctrlDown) {
                    NextSlection();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private boolean ConsumedInternalOnKeyReleased(char keyCode) {
        switch (keyCode) {
            case Key.LCTRL:
                _ctrlDown = false;
                return true;
            default:
                return false;
        }
    }

}
