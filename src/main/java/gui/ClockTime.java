package gui;

import kernel.Kernel;
import kernel.Logger;
import kernel.display.video.font.AFont;
import kernel.hardware.RTC;
import util.StrBuilder;

public class ClockTime extends TextField {
    private StrBuilder _sb;
    private int _lastHour;
    private int _lastMinute;
    private int _lastSecond;

    public ClockTime(int x, int y, int width, int height, AFont font, int fg, int bg) {
        super(x, y, width, height,
                0, 0, 0,
                fg, bg,
                font);

        this._sb = new StrBuilder(10);
        this._lastHour = -1;
        this._lastMinute = -1;
        this._lastSecond = -1;
    }

    private void setTimeStr() {
        int hours = RTC.readHour();
        int minutes = RTC.readMinute();
        int seconds = RTC.readSecond();

        _sb.clearKeepCapacity();
        _sb.append(Integer.toString(hours, 10).leftPad(2, '0'))
                .append(":")
                .append(Integer.toString(minutes, 10).leftPad(2, '0'))
                .append(":")
                .append(Integer.toString(seconds, 10).leftPad(2, '0'));

        String str = _sb.toString();
        setCursor(0, 0);
        super.addString(str);
        _lastHour = hours;
        _lastMinute = minutes;
        _lastSecond = seconds;
    }

    @Override
    public boolean isDirty() {
        int hours = RTC.readHour();
        int minutes = RTC.readMinute();
        int seconds = RTC.readSecond();

        boolean d = hours != _lastHour
                || minutes != _lastMinute
                || seconds != _lastSecond;
        if (d) {
            Logger.trace("ClockTime", "Dirty");
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
