package gui;

import kernel.display.video.VM13;

/**
 * Displays all VGA Mode 13h colors in a grid.
 */
public class ColorPalette implements IUiElement {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int boxSize;

    public ColorPalette(int x, int y, int width, int height, int boxSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.boxSize = boxSize;
    }

    @Override
    public void draw() {
        int x2 = x;
        int y2 = y;
        for (int i = 0; i < 256; i++) {
            for (int j = x2; j < x2 + boxSize; j++) {
                for (int j2 = y2; j2 < y2 + boxSize; j2++) {
                    VM13.putPixel(j, j2, (byte) i);
                }
            }

            x2 += boxSize;
            if (x2 + boxSize >= x + width) {
                x2 = x;
                y2 += boxSize;
            }
            if (y2 + boxSize >= y + height) {
                break;
            }
        }

    }

    @Override
    public void clearDrawing() {
        VM13.setRegion(x, y, width, height, (byte) 0);
    }
}
