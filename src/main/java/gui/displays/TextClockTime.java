package gui.displays;

import formats.fonts.Font8x8;
import gui.Widget;
import gui.components.TextField;
import kernel.display.GraphicsContext;
import kernel.hardware.RTC;
import util.StrBuilder;

public class TextClockTime extends Widget {
    private final int _bg;
    private final int _fg;
    private TextField _time;

    public TextClockTime(int x, int y, int z, int width, int height, int fg, int bg) {
        super("textclocktime", x, y, z, width, height);
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
    public void Draw(GraphicsContext display) {
        display.Rectangle(X, Y, Width, Height, _bg);
        UpdateText();
        _time.Draw(display);
    }

    private void UpdateText() {
        StrBuilder sb = new StrBuilder();
        int hours = RTC.ReadHour();
        int minutes = RTC.ReadMinute();
        int seconds = RTC.ReadSecond();
        sb.Append(Integer.toString(hours).LeftPad(2, '0'))
                .Append(':')
                .Append(Integer.toString(minutes).LeftPad(2, '0'))
                .Append(':')
                .Append(Integer.toString(seconds).LeftPad(2, '0'));
        _time.ClearText();
        _time.Write(sb.toString());

        _lastHour = hours;
        _lastMinute = minutes;
        _lastSecond = seconds;
    }

    private int _lastHour = -1;
    private int _lastMinute = -1;
    private int _lastSecond = -1;

    @Override
    public boolean NeedsRedraw() {
        return _lastSecond != RTC.ReadSecond()
                || _lastMinute != RTC.ReadMinute()
                || _lastHour != RTC.ReadHour();
    }

}
