package gui;

import kernel.display.ADisplay;
import kernel.hardware.Timer;
import util.vector.VecWindow;

public class WindowManager {
    private VecWindow _windows;
    private ADisplay _display;

    public WindowManager(ADisplay display) {
        _windows = new VecWindow();
        this._display = display;
    }

    public void AddWindow(ADisplayElement window) {
        _windows.add(window);
        _windows.SortByZ();
    }

    public void DrawWindows() {
        for (int i = 0; i < _windows.size(); i++) {
            ADisplayElement window = _windows.get(i);
            if (window.NeedsRedraw()) {
                window.Draw(_display);
            }
        }
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
}
