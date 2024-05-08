package gui;

import kernel.display.Bitmap;

public abstract class Widget {
    public int Width;
    public int Height;
    public boolean IsSelected;
    public String Name;
    public Bitmap RenderTarget;
    private boolean _needsRedraw;

    public Widget(String name, int width, int height) {
        Width = width;
        Height = height;
        Name = name;
        _needsRedraw = true;
        RenderTarget = new Bitmap(width, height, false);
    }

    public abstract void Draw();

    public void SetDirty() {
        _needsRedraw = true;
    }

    public void ClearDirty() {
        _needsRedraw = false;
    }

    public boolean NeedsRedraw() {
        return _needsRedraw;
    }
}
