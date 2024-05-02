package gui.displays.windows;

import formats.fonts.AFont;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.ADisplay;
import kernel.memory.MemoryManager;
import util.StrBuilder;

public class SystemInfo extends AWindow {
    private TextField _textField;
    private boolean _needsRedraw;
    private String _text;
    private int _drawEveryNth = 10;
    private int _drawCounter = 0;
    private StrBuilder _sb;

    public SystemInfo(
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
        int bg = Kernel.Display.Rgb(100, 100, 100);
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
                font);
        _needsRedraw = true;
        _sb = new StrBuilder(500);
        _text = UpdateText();
    }

    public void DrawContent(ADisplay display) {
        _text = UpdateText();
        _textField.ClearText();
        _textField.Write(_text);
        _textField.Draw(display);
    }

    private String UpdateText() {
        int consumedMemory = MemoryManager.GetUsedSpace();
        int freeMemory = MemoryManager.GetFreeSpace();
        int objectCount = MemoryManager.GetObjectCount();
        int emptyObjectCount = MemoryManager.GetEmptyObjectCount();

        _sb.ClearKeepCapacity();
        _sb.AppendLine("Memory:")
                .Append("    ").Append("Consumed: ").Append(consumedMemory).Append(" bytes").AppendLine()
                .Append("    ").Append("Free: ").Append(freeMemory).Append(" bytes").AppendLine()
                .Append("    ").Append("Objects: ").Append(objectCount).AppendLine()
                .Append("    ").Append("Empty Objects: ").Append(emptyObjectCount).AppendLine();

        return _sb.toString();
    }

    @Override
    public boolean NeedsRedraw() {
        _drawCounter++;
        if (_drawCounter >= _drawEveryNth) {
            _drawCounter = 0;
            _needsRedraw = true;
        } else {
            _needsRedraw = false;
        }
        return _needsRedraw;
    }
}
