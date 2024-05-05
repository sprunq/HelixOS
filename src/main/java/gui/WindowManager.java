package gui;

import formats.images.Image;
import gui.images.CursorModern;
import kernel.display.GraphicsContext;
import kernel.hardware.Timer;
import kernel.hardware.keyboard.Key;
import kernel.hardware.keyboard.KeyEvent;
import kernel.hardware.keyboard.KeyboardController;
import kernel.hardware.mouse.MouseController;
import kernel.hardware.mouse.MouseEvent;
import kernel.schedeule.Task;
import kernel.trace.logging.Logger;
import util.vector.VecWidget;

public class WindowManager extends Task {
    private GraphicsContext _ctx;
    private VecWidget _widgets;
    private Widget _selectedWindow;
    private int _drawTicksAvgN = 50;
    private int _drawTicksAvgCycle = 0;
    private int _drawTicksAvgSum = 0;

    private Image _cursorImage;

    static public int InfoAvgRenderTimeMs = 0;

    public WindowManager(GraphicsContext ctx) {
        super("_win_window_manager");
        _widgets = new VecWidget();
        this._ctx = ctx;
        _cursorImage = CursorModern.Load();
    }

    public void AddWindow(Widget window) {
        _widgets.add(window);
        _widgets.SortByZ();

        if (_selectedWindow == null && window.IsSelectable()) {
            _selectedWindow = window;
            _selectedWindow.SetSelected(true);
        }
    }

    public void NextSlection() {
        if (_selectedWindow == null) {
            _selectedWindow = _widgets.MaxSelectable();
        } else {
            _selectedWindow.SetSelected(false);
            _selectedWindow = _widgets.NextSelectable(_selectedWindow);
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

        for (int i = 0; i < _widgets.size(); i++) {
            Widget window = _widgets.get(i);

            if (window == null) {
                continue;
            }

            if (window.NeedsRedraw()) {
                window.Draw(_ctx);
            }
        }
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
        _ctx.Swap();

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

        if (!_ctx.Contains(_lastMouseX, _lastMouseY))
            return;
        if (!_ctx.Contains(_lastMouseX + _cursorImage.Width, _lastMouseY + _cursorImage.Height))
            return;

        _ctx.Bitmap(_lastMouseX, _lastMouseY, _cursorImage.PixelData);
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

    private MouseEvent _mouseEvent = new MouseEvent();

    public void DistributeMouseEvents() {
        if (!MouseController.ReadEvent(_mouseEvent)) {
            return;
        }

        SetDirtyAt(_lastMouseX, _lastMouseY);
        SetDirtyAt(_lastMouseX + _cursorImage.Width, _drawTicksAvgCycle + _cursorImage.Height);

        _lastMouseX += _mouseEvent.X_Delta;
        _lastMouseY -= _mouseEvent.Y_Delta;

        _lastMouseX = Math.Clamp(_lastMouseX, 0, _ctx.Width() - 5);
        _lastMouseY = Math.Clamp(_lastMouseY, 0, _ctx.Height() - 5);

        if (_mouseEvent.LeftButtonPressed()) {
            Logger.Trace("WIN", "Mouse Click at ".append(_lastMouseX).append(", ").append(_lastMouseY));
            // select window at xy
            for (int i = 0; i < _widgets.size(); i++) {
                Widget widget = _widgets.get(i);
                if (widget == null) {
                    continue;
                }
                if (widget.Contains(_lastMouseX, _lastMouseY)) {
                    if (_selectedWindow == widget) {
                        widget.LeftClickAt(_lastMouseX, _lastMouseY);
                        return;
                    }

                    if (_selectedWindow != null) {
                        _selectedWindow.SetSelected(false);
                    }
                    _selectedWindow = widget;
                    _selectedWindow.SetSelected(true);
                    Logger.Info("WIN", "Selected Widget ".append(_selectedWindow.Name));
                    break;
                }
            }
        } else if (_mouseEvent.RightButtonPressed()) {
            Logger.Trace("WIN", "Mouse Right Click at ".append(_lastMouseX).append(", ").append(_lastMouseY));
        } else if (_mouseEvent.MiddleButtonPressed()) {
            Logger.Trace("WIN", "Mouse Middle Click at ".append(_lastMouseX).append(", ").append(_lastMouseY));
        }
    }

    private void SetDirtyAt(int x, int y) {
        for (int i = 0; i < _widgets.size(); i++) {
            Widget window = _widgets.get(i);
            if (window == null) {
                continue;
            }
            if (window.Contains(x, y)) {
                window.SetDirty();
                break;
            }
        }
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
