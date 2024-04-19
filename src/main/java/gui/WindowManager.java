package gui;

import kernel.display.ADisplay;
import kernel.hardware.Timer;
import util.vector.VectorWindow;

public class WindowManager {
    private VectorWindow windows;
    private ADisplay display;

    public WindowManager(ADisplay display) {
        windows = new VectorWindow();
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
