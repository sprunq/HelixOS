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
    protected boolean _needsRedraw;

    public Widget(String name, int x, int y, int z, int width, int height) {
        X = x;
        Y = y;
        Z = z;
        Width = width;
        Height = height;
        Name = name;
        _needsRedraw = true;
    }

    public abstract void Draw(GraphicsContext ctx);

    public boolean NeedsRedraw() {
        return _needsRedraw;
    }

    public void SetDirty() {
        _needsRedraw = true;
    }

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

    public void LeftClickAt(int _lastMouseX, int _lastMouseY) {
    }
}
