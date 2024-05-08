package gui;

import formats.fonts.AFont;
import formats.fonts.Font9x16;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.GraphicsContext;
import kernel.trace.logging.Logger;

public abstract class Window {
    public final int FrameSize;
    public final int TitleBarSize;
    public int X;
    public int Y;
    public int Z;
    public int ContentX;
    public int ContentY;
    public int ContentWidth;
    public int ContentHeight;
    public TextField Title;

    public int Width;
    public int Height;
    public boolean IsSelected;
    public String Name;
    protected boolean _needsRedraw;

    public final int COL_BORDER;
    public final int COL_TITLEBAR;
    public final int COL_TITLEBAR_SELECTED;
    public final int COL_TITLE;

    public Window(String title, int x, int y, int z, int width, int height) {
        X = x;
        Y = y;
        Z = z;
        Width = width;
        Height = height;
        Name = title;
        _needsRedraw = true;

        COL_BORDER = Kernel.Display.Rgb(180, 180, 180);
        COL_TITLEBAR = Kernel.Display.Rgb(80, 80, 80);
        COL_TITLEBAR_SELECTED = Kernel.Display.Rgb(170, 190, 250);
        COL_TITLE = Kernel.Display.Rgb(255, 255, 255);
        FrameSize = 4;
        TitleBarSize = 20;

        ContentX = X + FrameSize;
        ContentY = Y + FrameSize + TitleBarSize;
        ContentWidth = Width - FrameSize * 2;
        ContentHeight = Height - FrameSize * 2 - TitleBarSize;
        AFont font = Font9x16.Instance;
        int shiftRight = 5;
        Title = new TextField(
                Width - shiftRight,
                font.Height(),
                0,
                0,
                0,
                COL_TITLE,
                COL_TITLEBAR,
                false,
                font);
        Title.Write(title);
    }

    public void Draw(GraphicsContext ctx) {
        DrawFrame(ctx);
        DrawTitleBar(ctx);
        DrawContent(ctx);
        _needsRedraw = false;
    }

    public abstract void DrawContent(GraphicsContext ctx);

    public void DrawFrame(GraphicsContext display) {
        if (IsSelected) {
            display.Rectangle(X, Y, Width, Height, COL_TITLEBAR_SELECTED);
        } else {
            display.Rectangle(X, Y, Width, Height, COL_BORDER);
        }
    }

    public void DrawTitleBar(GraphicsContext display) {
        display.Rectangle(X, Y, Width, TitleBarSize, COL_TITLEBAR);
        int centerFontH = (TitleBarSize - Title.Font.Height()) / 2;
        Title.Draw();
        display.Bitmap(X + 2, Y + centerFontH, Title.RenderTarget);
    }

    public boolean ContainsTitlebar(int x, int y) {
        return x >= X && x <= X + Width && y >= Y && y <= Y + TitleBarSize;
    }

    public boolean IsSelectable() {
        return true;
    }

    public boolean IsDraggable() {
        return true;
    }

    public void DragBy(int dragDiffX, int dragDiffY) {
        X += dragDiffX;
        Y += dragDiffY;
        ContentX += dragDiffX;
        ContentY += dragDiffY;
        SetDirty();
    }

    public boolean Contains(int x, int y) {
        return x >= X && x <= X + Width && y >= Y && y <= Y + Height;
    }

    public boolean NeedsRedraw() {
        return _needsRedraw;
    }

    public void SetDirty() {
        Logger.LogSerial("diry\n");
        _needsRedraw = true;
    }

    public void ClearDirty() {
        _needsRedraw = false;
    }

    public void SetSelected(boolean selected) {
        IsSelected = selected;
    }

    public boolean IsSelected() {
        return IsSelected;
    }

    // Interactions

    public void OnKeyPressed(char keyCode) {
    }

    public void OnKeyReleased(char keyCode) {
    }

    public void LeftClickAt(int _lastMouseX, int _lastMouseY) {
    }
}
