package gui.displays;

import formats.fonts.Font8x8;
import gui.ADisplayElement;
import gui.components.TextField;
import kernel.display.ADisplay;
import kernel.hardware.RTC;
import util.StrBuilder;

public class TextClockDate extends ADisplayElement {
    private final int _bg;
    private final int _fg;
    private TextField _time;

    public TextClockDate(int x, int y, int z, int width, int height, int fg, int bg) {
        super(x, y, z, width, height);
        _bg = bg;
        _fg = fg;

        _time = new TextField(
                X,
                Y,
                21,
                width,
                height,
                5,
                0,
                0,
                _fg,
                _bg,
                Font8x8.Instance);

    }

    @Override
    public void draw(ADisplay display) {
        display.fillrect(X, Y, Width, Height, _bg);
        updateText();
        _time.draw(display);
    }

    private void updateText() {
        StrBuilder sb = new StrBuilder();
        int day = RTC.readDayOfMonth();
        int month = RTC.readMonthOfYear();
        int year = RTC.readYearOfCentury();
        sb.append(Integer.toString(day).leftPad(2, '0'))
                .append('.')
                .append(Integer.toString(month).leftPad(2, '0'))
                .append('.')
                .append(Integer.toString(year).leftPad(2, '0'));
        _time.clearText();
        _time.write(sb.toString());

        _lastDay = day;
        _lastMonth = month;
        _lastYear = year;
    }

    private int _lastDay = -1;
    private int _lastMonth = -1;
    private int _lastYear = -1;

    @Override
    public boolean needsRedraw() {
        return _lastDay != RTC.readDayOfMonth()
                || _lastMonth != RTC.readMonthOfYear()
                || _lastYear != RTC.readYearOfCentury();
    }
}
