package gui.displays.windows;

import formats.fonts.AFont;
import gui.Window;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.GraphicsContext;
import kernel.trace.logging.LogEntry;
import kernel.trace.logging.Logger;

public class Logs extends Window {
    private final int COL_FATAL;
    private final int COL_ERROR;
    private final int COL_WARNING;
    private final int COL_INFO;
    private final int COL_TRACE;
    private int lastLogTick = -1;
    private TextField _textField;

    public Logs(
            String title,
            int x,
            int y,
            int z,
            int width,
            int height,
            int border,
            int charSpacing,
            int lineSpacing,
            AFont font) {
        super(x, y, z, width, height, title);

        COL_FATAL = Kernel.Display.Rgb(255, 0, 0);
        COL_ERROR = Kernel.Display.Rgb(200, 0, 0);
        COL_WARNING = Kernel.Display.Rgb(255, 230, 0);
        COL_INFO = Kernel.Display.Rgb(128, 200, 255);
        COL_TRACE = Kernel.Display.Rgb(100, 220, 100);

        int bg = Kernel.Display.Rgb(20, 20, 20);
        int fg = Kernel.Display.Rgb(255, 255, 255);
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
                true,
                font);
    }

    public void DrawContent(GraphicsContext ctx) {
        _textField.ClearText();
        int amountToDisplay = _textField.LineCount - 1;
        for (int i = amountToDisplay; i >= 0; i--) {
            LogEntry log = Logger.GetChronologicalLog(i);
            if (log != null) {
                String msg = log.Message();
                if (msg.length() != 0) {
                    String cat = log.Category();
                    byte level = (byte) log.Priority();
                    String time = log.TimeHMS();
                    int color = 0;
                    switch (level) {
                        case Logger.NONE:
                            color = 0;
                            break;
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
                    _textField.SetBrushColor(color);
                    if (time.length() != 0) {
                        _textField.Write("<");
                        _textField.Write(time);
                        _textField.Write("> ");
                    }
                    _textField.Write(cat);
                    _textField.Write(": ");
                    _textField.Write(msg);
                    _textField.NewLine();
                }
            }
        }
        _textField.Draw(ctx);
        lastLogTick = Logger.LogTicks();
    }

    @Override
    public boolean NeedsRedraw() {
        if (Logger.LogTicks() != lastLogTick) {
            _needsRedraw = true;
        }
        return super.NeedsRedraw();
    }

    @Override
    public void DragBy(int dragDiffX, int dragDiffY) {
        super.DragBy(dragDiffX, dragDiffY);
        _textField.DragBy(dragDiffX, dragDiffY);
    }
}
