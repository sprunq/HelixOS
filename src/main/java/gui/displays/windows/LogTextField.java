package gui.displays.windows;

import gui.components.TextField;
import kernel.Kernel;
import kernel.display.ADisplay;
import kernel.display.font.AFont;
import util.logging.LogEntry;
import util.logging.Logger;

public class LogTextField extends AWindow {
    private final int COL_FATAL;
    private final int COL_ERROR;
    private final int COL_WARNING;
    private final int COL_INFO;
    private final int COL_TRACE;
    private int lastLogTick = -1;
    private TextField _textField;

    public LogTextField(
            int x,
            int y,
            int z,
            int width,
            int height,
            int border,
            int charSpacing,
            int lineSpacing,
            AFont font) {
        super(x, y, z, width, height, "Log Entries");

        COL_FATAL = Kernel.Display.rgb(255, 0, 0);
        COL_ERROR = Kernel.Display.rgb(200, 0, 0);
        COL_WARNING = Kernel.Display.rgb(255, 230, 0);
        COL_INFO = Kernel.Display.rgb(128, 200, 255);
        COL_TRACE = Kernel.Display.rgb(100, 220, 100);

        int bg = Kernel.Display.rgb(20, 20, 20);
        int fg = Kernel.Display.rgb(255, 255, 255);
        _textField = new TextField(
                ContentX,
                ContentY,
                z,
                ContentWidth,
                ContentHeight,
                border,
                charSpacing,
                lineSpacing,
                fg,
                bg,
                font);
    }

    public void DrawContent(ADisplay display) {
        _textField.clearText();
        int amountToDisplay = _textField.LineCount - 1;
        for (int i = amountToDisplay; i >= 0; i--) {
            LogEntry log = Logger.getChronologicalLog(i);
            if (log != null) {
                String msg = log.getMessage();
                if (msg.length() != 0) {
                    String cat = log.getCategory();
                    byte level = (byte) log.getPriority();
                    String time = log.getTime_HMS();
                    int color = 0;
                    switch (level) {
                        case Logger.TRACE:
                            color = COL_TRACE;
                            break;
                        case Logger.INFO:
                            color = COL_INFO;
                            break;
                        case Logger.WARNING:
                            color = COL_WARNING;
                            break;
                        case Logger.ERROR:
                            color = COL_ERROR;
                            break;
                        case Logger.FATAL:
                            color = COL_FATAL;
                            break;
                        default:
                            Kernel.panic("LogTextField.draw: unknown log level");
                            break;
                    }
                    _textField.setBrushColor(color);
                    _textField.write("<");
                    _textField.write(time);
                    _textField.write("> ");
                    _textField.write(cat);
                    _textField.write(": ");
                    _textField.write(msg);
                    _textField.newLine();
                }
            }
        }
        _textField.draw(display);

        lastLogTick = Logger.getLogTicks();
    }

    @Override
    public boolean needsRedraw() {
        int logTicks = Logger.getLogTicks();
        return logTicks != lastLogTick;
    }
}
