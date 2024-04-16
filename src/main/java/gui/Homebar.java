package gui;

import kernel.Kernel;
import kernel.display.video.font.AFont;
import kernel.display.video.font.Font8x8;

public class Homebar implements IUIElement {
    public final int X;
    public final int Y;
    public final int Width;
    public final int Height;
    public ClockTime ClockTime;
    public ClockDate ClockDate;
    public TextField NameVersion;
    private int _bg;
    private int _fg;

    public Homebar(int x, int y, int width, int height) {
        this.X = x;
        this.Y = y;
        this.Width = width;
        this.Height = height;
        this._bg = Kernel.Display.rgb(0, 200, 200);
        this._fg = Kernel.Display.rgb(255, 255, 255);

        AFont clockFont = Font8x8.Instance;
        int clockWidth = clockFont.getWidth() * 9;
        int clockHeight = clockFont.getHeight() + 2;
        this.ClockTime = new ClockTime(
                X + Width - clockWidth - 5,
                Y + 2,
                clockWidth,
                clockHeight,
                clockFont,
                _fg,
                _bg);

        this.ClockDate = new ClockDate(
                X + Width - clockWidth - 5,
                Y + 11,
                clockWidth,
                clockHeight,
                clockFont,
                _fg,
                _bg);

        AFont nameFont = Font8x8.Instance;
        int nameWidth = nameFont.getWidth() * 5;
        int nameHeight = nameFont.getHeight() + 2;
        this.NameVersion = new TextField(
                5,
                Y + 7,
                nameWidth,
                nameHeight,
                0,
                0,
                0,
                _fg,
                _bg,
                nameFont);
        this.NameVersion.addString("TOOS");
    }

    @Override
    public boolean isDirty() {
        return ClockTime.isDirty() || ClockDate.isDirty() || NameVersion.isDirty();
    }

    @Override
    public void drawFg() {
        if (NameVersion.isDirty()) {
            NameVersion.drawFg();
        }
        if (ClockTime.isDirty()) {
            ClockTime.drawFg();
        }
        if (ClockDate.isDirty()) {
            ClockDate.drawFg();
        }
    }

    @Override
    public void drawBg() {
        Kernel.Display.fillrect(X, Y, Width, Height, _bg);
        NameVersion.drawBg();
        ClockTime.drawBg();
        ClockDate.drawBg();
    }
}
