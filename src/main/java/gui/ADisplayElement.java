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

    public abstract void Draw(ADisplay display);

    public abstract boolean NeedsRedraw();

    public boolean Contains(int x, int y) {
        return x >= X && x < X + Width && y >= Y && y < Y + Height;
    }
}
