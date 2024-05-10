package gui;

import formats.fonts.AFont;
import formats.fonts.Font9x16;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.Bitmap;
import kernel.schedule.Task;

public abstract class Window extends Task {
    public final int FrameSize;
    public final int TitleBarSize;
    public int X;
    public int Y;
    public final int ContentRelativeX;
    public final int ContentRelativeY;
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

    public Bitmap RenderTarget;

    public Window(String title, int x, int y, int width, int height) {
        super(title);
        X = x;
        Y = y;
        Width = width;
        Height = height;
        Name = title;
        _needsRedraw = true;

        RenderTarget = new Bitmap(width, height, false);

        COL_BORDER = Kernel.Display.Rgb(180, 180, 180);
        COL_TITLEBAR = Kernel.Display.Rgb(80, 80, 80);
        COL_TITLEBAR_SELECTED = Kernel.Display.Rgb(170, 190, 250);
        COL_TITLE = Kernel.Display.Rgb(255, 255, 255);
        FrameSize = 4;
        TitleBarSize = 20;

        ContentRelativeX = FrameSize;
        ContentRelativeY = FrameSize + TitleBarSize;
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

    @Override
    public void Run() {
        Update();
    }

    public abstract void Update();

    public void Draw() {
        DrawFrame();
        DrawTitleBar();
        DrawContent();
        ClearDirty();
    }

    public abstract void DrawContent();

    public void DrawFrame() {
        if (IsSelected) {
            RenderTarget.Rectangle(0, 0, Width, Height, COL_TITLEBAR_SELECTED);
        } else {
            RenderTarget.Rectangle(0, 0, Width, Height, COL_BORDER);
        }
    }

    public void DrawTitleBar() {
        RenderTarget.Rectangle(0, 0, Width - 1, TitleBarSize, COL_TITLEBAR);
        int centerFontH = (TitleBarSize - Title.Font.Height()) / 2;
        Title.Draw();
        RenderTarget.Blit(2, centerFontH, Title.RenderTarget, false);
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
        SetDirty();
    }

    public boolean Contains(int x, int y) {
        return x >= X && x <= X + Width && y >= Y && y <= Y + Height;
    }

    public boolean NeedsRedraw() {
        return _needsRedraw;
    }

    public void SetDirty() {
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
