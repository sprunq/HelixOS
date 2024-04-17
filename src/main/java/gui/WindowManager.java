package gui;

import kernel.display.vesa.VesaGraphics;
import util.vector.VectorWindow;

public class WindowManager {

    private VectorWindow windows;
    private VesaGraphics display;

    public WindowManager(VesaGraphics display) {
        windows = new VectorWindow();
        this.display = display;
    }

    public void addWindow(AWindow window) {
        windows.add(window);
        windows.SortByZ();
    }

    public void drawWindows() {
        for (int i = 0; i < windows.size(); i++) {
            AWindow window = windows.get(i);
            if (window.needsRedraw()) {
                window.draw(display);
            }
        }
    }
}
