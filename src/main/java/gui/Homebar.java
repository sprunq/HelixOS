package gui;

import kernel.display.video.font.Ascii8x8;
import kernel.display.video.font.IFont;
import kernel.display.video.m13.VideoMode13;
import kernel.hardware.RTC;

public class Homebar implements IUiElement {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public IFont font;
    public TextField clock;
    public TextField nameVersion;
    private byte backgroundColor;

    public Homebar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = (byte) 53;
        this.font = Ascii8x8.getInstance();
        this.clock = new TextField(320 - 153 - 1, 200 - 11, 153, 9, this.font, 1, 1, (byte) 15, backgroundColor);
        this.nameVersion = new TextField(5, 200 - 11, 100, 9, this.font, 1, 1, (byte) 15, backgroundColor);
        this.nameVersion.addString("TOOS v0.01");
    }

    @Override
    public void draw() {
        VideoMode13.setRegion(x, y, width, height, backgroundColor);
        nameVersion.draw();
        drawClock();
    }

    @Override
    public void clearDrawing() {
        clock.clearDrawing();
        nameVersion.clearDrawing();
        VideoMode13.setRegion(x, y, width, height, (byte) 0);
    }

    private void drawClock() {

        int day = RTC.readDay();
        String dayStr = Integer.toString(day, 10);
        String paddedDay = dayStr.leftPad(2, '0');

        int month = RTC.readMonthOfYear();
        String monthStr = Integer.toString(month, 10);
        String paddedMonth = monthStr.leftPad(2, '0');

        int year = RTC.readYearOfCentury();
        String yearStr = Integer.toString(year, 10);
        String paddedYear = yearStr.leftPad(2, '0');

        int hours = RTC.readHour();
        String hoursStr = Integer.toString(hours, 10);
        String paddedHours = hoursStr.leftPad(2, '0');

        int minutes = RTC.readMinute();
        String minutesStr = Integer.toString(minutes, 10);
        String paddedMinutes = minutesStr.leftPad(2, '0');

        int seconds = RTC.readSecond();
        String secondsStr = Integer.toString(seconds, 10);
        String paddedSeconds = secondsStr.leftPad(2, '0');

        clock.clearText();
        clock.addString(paddedDay);
        clock.addString("/");
        clock.addString(paddedMonth);
        clock.addString("/");
        clock.addString(paddedYear);
        clock.addString(" ");
        clock.addString(paddedHours);
        clock.addString(":");
        clock.addString(paddedMinutes);
        clock.addString(":");
        clock.addString(paddedSeconds);
        clock.draw();
    }
}
