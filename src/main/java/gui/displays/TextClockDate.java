package gui.displays;

import formats.fonts.Font8x8;
import gui.Widget;
import gui.components.TextField;
import kernel.display.GraphicsContext;
import kernel.hardware.RTC;
import util.StrBuilder;

public class TextClockDate extends Widget {
    private final int _bg;
    private final int _fg;
    private TextField _time;

    public TextClockDate(int x, int y, int z, int width, int height, int fg, int bg) {
        super("textclockdate", x, y, z, width, height);
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
                false,
                Font8x8.Instance);

    }

    @Override
    public void Draw(GraphicsContext display) {
        display.Rectangle(X, Y, Width, Height, _bg);
        UpdateText();
        _time.Draw(display);
    }

    private void UpdateText() {
        StrBuilder sb = new StrBuilder();
        int day = RTC.ReadDayOfMonth();
        int month = RTC.ReadMonthOfYear();
        int year = RTC.ReadYearOfCentury();
        sb.Append(Integer.toString(day).LeftPad(2, '0'))
                .Append('.')
                .Append(Integer.toString(month).LeftPad(2, '0'))
                .Append('.')
                .Append(Integer.toString(year).LeftPad(2, '0'));
        _time.ClearText();
        _time.Write(sb.toString());

        _lastDay = day;
        _lastMonth = month;
        _lastYear = year;
    }

    private int _lastDay = -1;
    private int _lastMonth = -1;
    private int _lastYear = -1;

    @Override
    public boolean NeedsRedraw() {
        return _lastDay != RTC.ReadDayOfMonth()
                || _lastMonth != RTC.ReadMonthOfYear()
                || _lastYear != RTC.ReadYearOfCentury()
                || super.NeedsRedraw();
    }
}
