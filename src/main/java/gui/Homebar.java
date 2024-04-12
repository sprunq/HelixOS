package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font5x7;
import kernel.display.video.font.Font8x8;
import kernel.hardware.RTC;
import util.StrBuilder;

public class Homebar implements IUiElement {
    public final int X;
    public final int Y;
    public final int Width;
    public final int Height;
    public TextField Clock;
    public TextField NameVersion;
    private byte _backgroundColor;

    public Homebar(int x, int y, int width, int height) {
        this.X = x;
        this.Y = y;
        this.Width = width;
        this.Height = height;
        this._backgroundColor = VM13.frgb(0.1, 0.2, 0.5);

        this.Clock = new TextField(
                320 - 120 - 1,
                200 - 11,
                120,
                9,
                0,
                0,
                0,
                _backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0),
                Font5x7.Instance);

        this.NameVersion = new TextField(
                5,
                200 - 11,
                100,
                9,
                0,
                0,
                0,
                _backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0),
                Font8x8.Instance);
        this.NameVersion.addString("TOOS");
    }

    @Override
    public void draw() {
        VM13.fillrect(X, Y, Width, Height, _backgroundColor);
        NameVersion.draw();
        drawClock();
    }

    private void drawClock() {
        int day = RTC.readDay();
        int month = RTC.readMonthOfYear();
        int year = RTC.readYearOfCentury();
        int hours = RTC.readHour();
        int minutes = RTC.readMinute();
        int seconds = RTC.readSecond();

        StrBuilder sb = new StrBuilder()
                .append(Integer.toString(day, 10).leftPad(2, '0'))
                .append("/")
                .append(Integer.toString(month, 10).leftPad(2, '0'))
                .append("/")
                .append(Integer.toString(year, 10).leftPad(2, '0'))
                .append(" ")
                .append(Integer.toString(hours, 10).leftPad(2, '0'))
                .append(":")
                .append(Integer.toString(minutes, 10).leftPad(2, '0'))
                .append(":")
                .append(Integer.toString(seconds, 10).leftPad(2, '0'));

        Clock.clearText();
        Clock.addString(sb.toString());
        Clock.draw();
    }
}
