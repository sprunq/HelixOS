package gui.windows;

import kernel.display.ADisplay;

public abstract class AWindow {
    protected int X;
    protected int Y;
    protected int Z;
    protected int Width;
    protected int Height;

    public AWindow(int x, int y, int z, int width, int height) {
        X = x;
        Y = y;
        Z = z;
        Width = width;
        Height = height;
    }

    public abstract void draw(ADisplay display);

    public abstract boolean needsRedraw();

    public int X() {
        return X;
    }

    public int Y() {
        return Y;
    }

    public int Z() {
        return Z;
    }

    public int Width() {
        return Width;
    }

    public int Height() {
        return Height;
    }

    public boolean contains(int x, int y) {
        return x >= X && x < X + Width && y >= Y && y < Y + Height;
    }
}
