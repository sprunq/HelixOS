package gui;

import kernel.display.ADisplay;
import kernel.hardware.Timer;
import util.vector.VecWindow;

public class WindowManager {
    private VecWindow windows;
    private ADisplay display;

    public WindowManager(ADisplay display) {
        windows = new VecWindow();
        this.display = display;
    }

    public void AddWindow(ADisplayElement window) {
        windows.add(window);
        windows.SortByZ();
    }

    public void DrawWindows() {
        for (int i = 0; i < windows.size(); i++) {
            ADisplayElement window = windows.get(i);
            if (window.NeedsRedraw()) {
                window.Draw(display);
            }
        }
    }

    public void StaticDisplayFor(int ms) {
        DrawWindows();
        display.Swap();
        Timer.Sleep(ms);
        display.ClearScreen();
    }
}
