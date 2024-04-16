package gui;

import kernel.Kernel;
import kernel.Logger;
import kernel.display.video.font.AFont;
import kernel.hardware.RTC;
import util.StrBuilder;

public class ClockDate extends TextField {
    private StrBuilder _sb;
    private int _day;
    private int _month;
    private int _year;

    public ClockDate(int x, int y, int width, int height, AFont font, int fg, int bg) {
        super(x, y, width, height,
                0, 0, 0,
                fg, bg,
                font);

        this._sb = new StrBuilder(10);
    }

    private void setTimeStr() {
        int day = RTC.readDayOfMonth();
        int month = RTC.readMonthOfYear();
        int year = RTC.readYearOfCentury();

        _sb.clearKeepCapacity();
        _sb.append(Integer.toString(day, 10).leftPad(2, '0'))
                .append("/")
                .append(Integer.toString(month, 10).leftPad(2, '0'))
                .append("/")
                .append(Integer.toString(year, 10).leftPad(2, '0'));

        String str = _sb.toString();
        setCursor(0, 0);
        super.addString(str);
        _day = day;
        _month = month;
        _year = year;
    }

    @Override
    public boolean isDirty() {
        int day = RTC.readDayOfMonth();
        int month = RTC.readMonthOfYear();
        int year = RTC.readYearOfCentury();

        boolean d = day != _day
                || month != _month
                || year != _year;
        if (d) {
            Logger.trace("ClockDate", "Dirty");
        }
        return d;
    }

    @Override
    public void drawFg() {
        setTimeStr();
        super.drawFg();
    }

    @Override
    public void drawBg() {
        Kernel.Display.fillrect(X, Y, Width, Height, _bg);
    }
}
