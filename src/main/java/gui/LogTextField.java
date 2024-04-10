package gui;

import kernel.LogEntry;
import kernel.Logger;
import kernel.display.video.VM13;
import kernel.display.video.font.AFont;

public class LogTextField extends TextField {
    private final byte COL_FALLBACK;
    private final byte COL_FATAL;
    private final byte COL_ERROR;
    private final byte COL_WARNING;
    private final byte COL_INFO;
    private final byte COL_TRACE;
    private final byte COL_WHITE;

    public LogTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            byte backGroundColor) {
        super(x, y, width, height, border, charSpacing, lineSpacing, backGroundColor, VM13.frgb(1.0, 1.0, 1.0), font);
        COL_FALLBACK = VM13.frgb(1.0, 0.5, 1.0);
        COL_FATAL = VM13.frgb(0.6, 0.0, 0.0);
        COL_ERROR = VM13.frgb(0.9, 0.1, 0.1);
        COL_WARNING = VM13.frgb(1.0, 0.7, 0.0);
        COL_INFO = VM13.frgb(0.5, 0.7, 1.0);
        COL_TRACE = VM13.frgb(0.4, 0.8, 0.4);
        COL_WHITE = VM13.frgb(1.0, 1.0, 1.0);
    }

    public void draw() {
        clearText();
        int amountToDisplay = lines - 1;
        for (int i = amountToDisplay; i >= 0; i--) {
            LogEntry log = Logger.getChronologicalLog(i);
            if (log != null) {
                String msg = log.getMessage();
                if (msg.length() != 0) {
                    String cat = log.getCategory();
                    byte level = (byte) log.getPriority();
                    byte color = 0;
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
                            color = COL_FALLBACK;
                            break;
                    }
                    setBrushColor(color);
                    addString("<");
                    addString(cat);
                    addString("> ");
                    addString(msg);
                    newLine();
                }
            }
        }
        setCursor(0, 0);
        clearLine(0);
        setBrushColor(COL_WHITE);
        addString("Log Entries");
        newLine();
        super.draw();
    }

}
