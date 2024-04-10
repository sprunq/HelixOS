package gui;

import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import util.MathH;

public class MultiWindow implements IUiElement, IKeyboardEventListener {
    private IUiElement[] windows;
    private int displayIndex;
    private int windowsAmount;

    private boolean ctrlDown = false;

    public MultiWindow(int windowsCount) {
        this.windows = new IUiElement[windowsCount];
        this.displayIndex = 0;
        this.windowsAmount = 0;
    }

    public void addWindow(IUiElement window) {
        if (windowsAmount < windows.length) {
            windows[windowsAmount] = window;
            windowsAmount++;
        }
    }

    public void clearDrawing() {
        int i = MathH.abs(displayIndex) % windows.length;
        windows[i].clearDrawing();
    }

    public void draw() {
        int i = MathH.abs(displayIndex) % windows.length;
        windows[i].draw();
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
                    displayIndex++;
                    return true;
                case Key.PAGE_DOWN:
                    displayIndex--;
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
