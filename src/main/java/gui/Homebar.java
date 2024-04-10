package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font5x7;
import kernel.display.video.font.Font8x8;
import kernel.hardware.RTC;
import util.StrBuilder;

public class Homebar implements IUiElement {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public TextField clock;
    public TextField nameVersion;
    private byte backgroundColor;

    public Homebar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = VM13.frgb(0.1, 0.2, 0.5);

        this.clock = new TextField(
                320 - 120 - 1,
                200 - 11,
                120,
                9,
                0,
                0,
                0,
                backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0),
                Font5x7.Instance);

        this.nameVersion = new TextField(
                5,
                200 - 11,
                100,
                9,
                0,
                0,
                0,
                backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0),
                Font8x8.Instance);
        this.nameVersion.addString("TOOS");
    }

    @Override
    public void draw() {
        VM13.fillrect(x, y, width, height, backgroundColor);
        nameVersion.draw();
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

        clock.clearText();
        clock.addString(sb.toString());
        clock.draw();
    }
}
