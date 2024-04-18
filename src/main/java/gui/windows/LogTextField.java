package gui.windows;

import kernel.Kernel;
import kernel.display.ADisplay;
import kernel.display.tm3.AFont;
import util.logging.LogEntry;
import util.logging.Logger;

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
            int z,
            int width,
            int height,
            int border,
            int charSpacing,
            int lineSpacing,
            AFont font) {
        super(x,
                y,
                z,
                width,
                height,
                border,
                charSpacing,
                lineSpacing,
                Kernel.Display.rgb(0, 255, 0),
                Kernel.Display.rgb(0, 13, 40),
                font);
        COL_FATAL = Kernel.Display.rgb(255, 0, 0);
        COL_ERROR = Kernel.Display.rgb(200, 0, 0);
        COL_WARNING = Kernel.Display.rgb(255, 230, 0);
        COL_INFO = Kernel.Display.rgb(128, 200, 255);
        COL_TRACE = Kernel.Display.rgb(100, 220, 100);
        COL_WHITE = Kernel.Display.rgb(255, 255, 255);
    }

    @Override
    public void draw(ADisplay display) {
        clearText();
        int amountToDisplay = LineCount - 1;
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
                    setBrushColor(color);
                    write("<");
                    write(time);
                    write("> ");
                    write(cat);
                    write(": ");
                    write(msg);
                    newLine();
                }
            }
        }
        setCursor(0, 0);
        clearLine(0);
        setBrushColor(COL_WHITE);
        write("Log Entries");
        newLine();
        lastLogTick = Logger.getLogTicks();
        super.draw(display);
    }

    @Override
    public boolean needsRedraw() {
        int logTicks = Logger.getLogTicks();
        return logTicks != lastLogTick;
    }
}
