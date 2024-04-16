package gui;

import kernel.Logger;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import util.MathH;

public class MultiWindow implements IUiElement, IKeyboardEventListener {
    private IUiElement[] _windows;
    private int _displayIndex;
    private int _windowsAmount;

    private boolean ctrlDown = false;

    public MultiWindow(int windowsCount) {
        this._windows = new IUiElement[windowsCount];
        this._displayIndex = 0;
        this._windowsAmount = 0;
    }

    public void addWindow(IUiElement window) {
        if (_windowsAmount < _windows.length) {
            _windows[_windowsAmount] = window;
            _windowsAmount++;
        }
    }

    public void draw() {
        int i = MathH.abs(_displayIndex) % _windows.length;
        _windows[i].draw();
    }

    @Override
    public boolean onKeyPressed(char keyCode) {
        if (keyCode == Key.LCTRL) {
            ctrlDown = true;
            return false;
        }
        if (ctrlDown) {
            switch ((int) keyCode) {
                case Key.PAGE_UP:
                    Logger.trace("Win", "Next window");
                    _displayIndex++;
                    return true;
                case Key.PAGE_DOWN:
                    Logger.trace("Win", "Prev window");
                    _displayIndex--;
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        if (keyCode == Key.LCTRL) {
            ctrlDown = false;
            return false;
        }
        return false;
    }

}
