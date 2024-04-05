package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font5x7;
import kernel.display.video.font.Font8x8;
import kernel.hardware.RTC;

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
                Font5x7.Instance, 0, 0,
                backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0));

        this.nameVersion = new TextField(
                5, 200 - 11,
                100, 9,
                Font8x8.Instance, 0, 0,
                backgroundColor,
                VM13.frgb(1.0, 1.0, 1.0));
        this.nameVersion.addString("TOOS");
    }

    @Override
    public void draw() {
        VM13.setRegion(x, y, width, height, backgroundColor);
        nameVersion.draw();
        drawClock();
    }

    @Override
    public void clearDrawing() {
        clock.clearDrawing();
        nameVersion.clearDrawing();
        VM13.setRegion(x, y, width, height, (byte) 0);
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
