package gui.windows;

import gui.AWindow;
import kernel.display.ADisplay;

public class TestWindow extends AWindow {

    private int color;

    public TestWindow(int x, int y, int z, int width, int height, int color) {
        super(x, y, z, width, height);
        this.color = color;
    }

    @Override
    public void draw(ADisplay display) {
        for (int i = 0; i < Width; i++) {
            for (int j = 0; j < Height; j++) {
                display.setPixel(X + i, Y + j, color);
            }
        }
    }

    @Override
    public boolean needsRedraw() {
        return true;
    }
}
