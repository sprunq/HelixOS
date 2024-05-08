package gui.displays.windows;

import formats.fonts.AFont;
import gui.Window;
import gui.WindowManager;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.GraphicsContext;
import kernel.memory.GarbageCollector;
import kernel.memory.Memory;
import kernel.memory.MemoryManager;
import util.StrBuilder;

public class SystemInfo extends Window {
    private TextField _textField;
    private String _text;
    private int _drawEveryNth = 1;
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
        super(title, x, y, z, width, height);
        int bg = Kernel.Display.Rgb(100, 100, 100);
        int fg = Kernel.Display.Rgb(255, 255, 255);
        _textField = new TextField(
                ContentWidth,
                ContentHeight,
                border,
                charSpacing,
                lineSpacing,
                fg,
                bg,
                false,
                font);
        _sb = new StrBuilder(500);
    }

    public void DrawContent(GraphicsContext ctx) {
        UpdateText();
        if (_textField.NeedsRedraw()) {
            _textField.Draw();
        }
        ctx.Bitmap(ContentX, ContentY, _textField.RenderTarget);
    }

    private void UpdateText() {
        int consumedMemory = MemoryManager.GetUsedSpace();
        int freeMemory = MemoryManager.GetFreeSpace();
        int objectCount = MemoryManager.GetObjectCount();
        int emptyObjectCount = MemoryManager.GetEmptyObjectCount();

        _sb.ClearKeepCapacity();

        _sb.AppendLine("Window Manger:")
                .Append("  ").Append("Average Draw Time ").Append(WindowManager.InfoAvgRenderTimeMs).Append(" ms")
                .AppendLine();

        _sb.AppendLine();
        _sb.AppendLine("Memory:")
                .Append("  ").Append("Consumed: ").Append(Memory.FormatBytesToKb(consumedMemory)).AppendLine()
                .Append("  ").Append("Free: ").Append(Memory.FormatBytesToKb(freeMemory)).AppendLine()
                .Append("  ").Append("Objects: ").Append(objectCount).AppendLine()
                .Append("  ").Append("Empty Objects: ").Append(emptyObjectCount).AppendLine();

        _sb.AppendLine();
        _sb.AppendLine("GC:")
                .Append("  ").Append("Last Run Time: ")
                .Append(GarbageCollector.InfoLastRunTimeMs).Append(" ms").AppendLine()
                .Append("  ").Append("Last Run Marked: ")
                .Append(GarbageCollector.InfoLastRunCollectedObjects).AppendLine()
                .Append("  ").Append("Last Run Collected: ")
                .Append(Memory.FormatBytes(GarbageCollector.InfoLastRunCollectedBytes)).AppendLine()
                .Append("  ").Append("Last Run Compacted: ")
                .Append(GarbageCollector.InfoLastRunCompactedEmptyObjects).AppendLine();

        _text = _sb.toString();
        _textField.ClearText();
        _textField.Write(_text);
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

    @Override
    public void DragBy(int dragDiffX, int dragDiffY) {
        super.DragBy(dragDiffX, dragDiffY);
        // _textField.DragBy(dragDiffX, dragDiffY);
    }
}
