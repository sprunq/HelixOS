package gui;

import kernel.Kernel;

/**
 * Displays all VGA Mode 13h colors in a grid.
 */
public class ColorPalette implements IUiElement {
    public final int X;
    public final int Y;
    public final int Width;
    public final int Height;
    public final int BoxSize;

    public ColorPalette(int x, int y, int width, int height, int boxSize) {
        this.X = x;
        this.Y = y;
        this.Width = width;
        this.Height = height;
        this.BoxSize = boxSize;
    }

    @Override
    public void draw() {
        int x2 = X;
        int y2 = Y;
        for (int i = 0; i < 256; i++) {
            for (int j = x2; j < x2 + BoxSize; j++) {
                for (int j2 = y2; j2 < y2 + BoxSize; j2++) {
                    Kernel.Vesa.setPixel(j, j2, 100);
                }
            }

            x2 += BoxSize;
            if (x2 + BoxSize >= X + Width) {
                x2 = X;
                y2 += BoxSize;
            }
            if (y2 + BoxSize >= Y + Height) {
                break;
            }
        }
    }
}
