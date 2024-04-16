package gui;

import kernel.LogEntry;
import kernel.Logger;
import kernel.display.vesa.VESAGraphics;
import kernel.display.video.VM13;
import kernel.display.video.font.AFont;

public class LogTextField extends TextField {
    private final int COL_FALLBACK;
    private final int COL_FATAL;
    private final int COL_ERROR;
    private final int COL_WARNING;
    private final int COL_INFO;
    private final int COL_TRACE;
    private final int COL_WHITE;

    public LogTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            int backGroundColor) {
        super(x, y, width, height, border, charSpacing, lineSpacing, backGroundColor, VESAGraphics.rgb24(255, 255, 255),
                font);
        COL_FALLBACK = VESAGraphics.rgb24(255, 255, 255);
        COL_FATAL = VESAGraphics.rgb24(255, 255, 255);
        COL_ERROR = VESAGraphics.rgb24(255, 255, 255);
        COL_WARNING = VESAGraphics.rgb24(255, 255, 255);
        COL_INFO = VESAGraphics.rgb24(255, 255, 255);
        COL_TRACE = VESAGraphics.rgb24(255, 255, 255);
        COL_WHITE = VESAGraphics.rgb24(255, 255, 255);
    }

    public void draw() {
        clearText();
        int amountToDisplay = LineCount - 1;
        for (int i = amountToDisplay; i >= 0; i--) {
            LogEntry log = Logger.getChronologicalLog(i);
            if (log != null) {
                String msg = log.getMessage();
                if (msg.length() != 0) {
                    String cat = log.getCategory();
                    byte level = (byte) log.getPriority();
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
