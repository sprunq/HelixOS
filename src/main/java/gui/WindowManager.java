package gui;

import kernel.display.GraphicsContext;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.Key;
import kernel.hardware.keyboard.KeyEvent;
import kernel.hardware.keyboard.KeyboardController;
import kernel.schedeule.Task;
import kernel.trace.logging.Logger;
import util.vector.VecWidget;

public class WindowManager extends Task {
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
        DrawWindows();
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

    private boolean _ctrlDown = false;
    private KeyEvent _keyEvent = new KeyEvent();

    private boolean ConsumedInternalOnKeyPressed(char keyCode) {
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

    private boolean ConsumedInternalOnKeyReleased(char keyCode) {
        switch ((int) keyCode) {
            case Key.LCTRL:
                _ctrlDown = false;
                return true;
            default:
                return false;
        }
    }

}
