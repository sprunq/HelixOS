package gui;

import kernel.LogEntry;
import kernel.Logger;
import kernel.display.video.VM13;
import kernel.display.video.font.IFont;

public class LogTextField extends TextField {
    public LogTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            IFont font,
            int charSpacing,
            int lineSpacing,
            byte backGroundColor) {
        super(x, y, width, height, border, charSpacing, lineSpacing, backGroundColor, VM13.frgb(1.0, 1.0, 1.0), font);
    }

    public void draw() {
        clearText();
        for (int i = Logger.getNumberOfLogs() - 1; i >= 0; i--) {
            LogEntry log = Logger.getChronologicalLog(i);
            if (log != null) {
                String msg = log.getMessage();
                if (msg.length() != 0) {
                    byte level = (byte) log.getPriority();
                    byte color = 0;
                    switch (level) {
                        case Logger.DEBUG:
                            color = VM13.frgb(0.4, 0.8, 0.4);
                            break;
                        case Logger.INFO:
                            color = VM13.frgb(0.5, 0.7, 1.0);
                            break;
                        case Logger.WARNING:
                            color = VM13.frgb(1.0, 0.7, 0.0);
                            break;
                        case Logger.ERROR:
                            color = VM13.frgb(0.9, 0.1, 0.1);
                            break;
                        case Logger.FATAL:
                            color = VM13.frgb(0.6, 0.0, 0.0);
                            break;
                        default:
                            color = VM13.frgb(1.0, 0.5, 1.0);
                            break;
                    }
                    setBrushColor(color);
                    addString("- ");
                    addString(msg);
                }
                newLine();
            }
        }
        super.draw();
    }

}
