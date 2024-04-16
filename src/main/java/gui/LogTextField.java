package gui;

import kernel.Kernel;
import kernel.LogEntry;
import kernel.Logger;
import kernel.display.video.font.AFont;

public class LogTextField extends TextField {
    private final int COL_FATAL;
    private final int COL_ERROR;
    private final int COL_WARNING;
    private final int COL_INFO;
    private final int COL_TRACE;
    private final int COL_WHITE;
    private int lastLogTick = -1;

    public LogTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            int charSpacing,
            int lineSpacing,
            AFont font) {
        super(x,
                y,
                width,
                height,
                border,
                charSpacing,
                lineSpacing,
                Kernel.Display.rgb(0, 255, 0),
                Kernel.Display.rgb(100, 100, 100),
                font);

        COL_FATAL = Kernel.Display.rgb(255, 0, 0);
        COL_ERROR = Kernel.Display.rgb(200, 0, 0);
        COL_WARNING = Kernel.Display.rgb(255, 230, 0);
        COL_INFO = Kernel.Display.rgb(128, 200, 255);
        COL_TRACE = Kernel.Display.rgb(100, 220, 100);
        COL_WHITE = Kernel.Display.rgb(255, 255, 255);
    }

    public void drawFg() {
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
                            Kernel.panic("LogTextField.draw: unknown log level");
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
        lastLogTick = Logger.getLogTicks();
        super.drawFg();
    }

    @Override
    public boolean isDirty() {
        int logTicks = Logger.getLogTicks();
        boolean dirty = logTicks != lastLogTick;
        if (dirty) {
            Logger.trace("LogField", "Dirty");
        }
        return dirty;
    }
}
