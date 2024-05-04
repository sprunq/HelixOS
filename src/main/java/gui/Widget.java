package gui;

import kernel.display.GraphicsContext;

public abstract class Widget {
    public int X;
    public int Y;
    public int Z;
    public int Width;
    public int Height;
    public boolean IsSelected;
    public String Name;

    public Widget(String name, int x, int y, int z, int width, int height) {
        X = x;
        Y = y;
        Z = z;
        Width = width;
        Height = height;
        Name = name;
    }

    public abstract void Draw(GraphicsContext ctx);

    public abstract boolean NeedsRedraw();

    public boolean Contains(int x, int y) {
        return x >= X && x < X + Width && y >= Y && y < Y + Height;
    }

    public void SetSelected(boolean selected) {
        IsSelected = selected;
    }

    public boolean IsSelected() {
        return IsSelected;
    }

    public boolean IsSelectable() {
        return false;
    }

    public void OnKeyPressed(char keyCode) {

    }

    public void OnKeyReleased(char keyCode) {
    }
}
