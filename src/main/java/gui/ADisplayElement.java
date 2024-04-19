package gui;

import kernel.display.ADisplay;

public abstract class ADisplayElement {
    public int X;
    public int Y;
    public int Z;
    public int Width;
    public int Height;

    public ADisplayElement(int x, int y, int z, int width, int height) {
        X = x;
        Y = y;
        Z = z;
        Width = width;
        Height = height;
    }

    public abstract void draw(ADisplay display);

    public abstract boolean needsRedraw();

    public boolean contains(int x, int y) {
        return x >= X && x < X + Width && y >= Y && y < Y + Height;
    }
}
