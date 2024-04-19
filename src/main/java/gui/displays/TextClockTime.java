package gui.displays;

import gui.ADisplayElement;
import gui.components.TextField;
import kernel.display.ADisplay;
import kernel.display.font.Font8x8;
import kernel.hardware.RTC;
import util.StrBuilder;

public class TextClockTime extends ADisplayElement {
    private final int _bg;
    private final int _fg;
    private TextField _time;

    public TextClockTime(int x, int y, int z, int width, int height, int fg, int bg) {
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
        int hours = RTC.readHour();
        int minutes = RTC.readMinute();
        int seconds = RTC.readSecond();
        sb.append(Integer.toString(hours).leftPad(2, '0'))
                .append(':')
                .append(Integer.toString(minutes).leftPad(2, '0'))
                .append(':')
                .append(Integer.toString(seconds).leftPad(2, '0'));
        _time.clearText();
        _time.write(sb.toString());

        _lastHour = hours;
        _lastMinute = minutes;
        _lastSecond = seconds;
    }

    private int _lastHour = -1;
    private int _lastMinute = -1;
    private int _lastSecond = -1;

    @Override
    public boolean needsRedraw() {
        return _lastSecond != RTC.readSecond()
                || _lastMinute != RTC.readMinute()
                || _lastHour != RTC.readHour();
    }

}
