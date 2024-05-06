package gui;

import formats.fonts.AFont;
import formats.fonts.Font9x16;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.GraphicsContext;

public abstract class Window extends Widget {
    public final int FrameSize;
    public final int TitleBarSize;
    public int ContentX;
    public int ContentY;
    public int ContentWidth;
    public int ContentHeight;
    public TextField Title;

    public final int COL_BORDER;
    public final int COL_TITLEBAR;
    public final int COL_TITLEBAR_SELECTED;
    public final int COL_TITLE;

    public Window(int x, int y, int z, int width, int height, String title) {
        super(title, x, y, z, width, height);

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
        int centerFontH = (TitleBarSize - font.Height()) / 2;
        int shiftRight = 5;
        Title = new TextField(X + 5,
                Y + centerFontH,
                Z,
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
    public void Draw(GraphicsContext ctx) {
        DrawFrame(ctx);
        DrawTitleBar(ctx);
        DrawContent(ctx);
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
        Title.Draw(display);
    }

    public boolean ContainsTitlebar(int x, int y) {
        return x >= X && x <= X + Width && y >= Y && y <= Y + TitleBarSize;
    }

    @Override
    public boolean IsSelectable() {
        return true;
    }

    @Override
    public void DragBy(int dragDiffX, int dragDiffY) {
        super.DragBy(dragDiffX, dragDiffY);
        Title.DragBy(dragDiffX, dragDiffY);
        ContentX += dragDiffX;
        ContentY += dragDiffY;
    }
}
