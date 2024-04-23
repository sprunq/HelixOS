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

    public void addWindow(ADisplayElement window) {
        windows.add(window);
        windows.SortByZ();
    }

    public void drawWindows() {
        for (int i = 0; i < windows.size(); i++) {
            ADisplayElement window = windows.get(i);
            if (window.needsRedraw()) {
                window.draw(display);
            }
        }
    }

    public void staticDisplayFor(int ms) {
        drawWindows();
        display.swap();
        Timer.sleep(ms);
        display.clearScreen();
    }
}
