package gui.displays.windows;

import formats.fonts.AFont;
import formats.fonts.Font9x16;
import gui.ADisplayElement;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.ADisplay;

public abstract class AWindow extends ADisplayElement {
    public final int FrameSize;
    public final int TitleBarSize;
    public int ContentX;
    public int ContentY;
    public int ContentWidth;
    public int ContentHeight;
    public TextField Title;

    public final int COL_BORDER;
    public final int COL_TITLEBAR;
    public final int COL_TITLE;

    public AWindow(int x, int y, int z, int width, int height, String title) {
        super(x, y, z, width, height);

        COL_BORDER = Kernel.Display.Rgb(180, 180, 180);
        COL_TITLEBAR = Kernel.Display.Rgb(80, 80, 80);
        COL_TITLE = Kernel.Display.Rgb(255, 255, 255);
        FrameSize = 2;
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
                font);
        Title.Write(title);
    }

    @Override
    public void Draw(ADisplay display) {
        DrawFrame(display);
        DrawTitleBar(display);
        DrawContent(display);
    }

    public abstract void DrawContent(ADisplay display);

    public void DrawFrame(ADisplay display) {
        display.Rectangle(X, Y, Width, Height, COL_BORDER);
    }

    public void DrawTitleBar(ADisplay display) {
        display.Rectangle(X, Y, Width, TitleBarSize, COL_TITLEBAR);
        Title.Draw(display);
    }
}
