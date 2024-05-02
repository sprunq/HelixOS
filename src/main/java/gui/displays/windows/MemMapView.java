package gui.displays.windows;

import formats.fonts.AFont;
import gui.components.TextField;
import kernel.Kernel;
import kernel.bios.call.MemMap;
import kernel.bios.call.MemMapEntry;
import kernel.display.ADisplay;
import util.StrBuilder;

public class MemMapView extends AWindow {
    private TextField _textField;
    private boolean _needsRedraw;
    private String _memmapText;

    public MemMapView(
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
        _memmapText = BuildMemMapStr();
        _needsRedraw = true;
    }

    public void DrawContent(ADisplay display) {
        _textField.ClearText();
        _textField.Write(_memmapText);
        _textField.Draw(display);
        _needsRedraw = false;
    }

    private String BuildMemMapStr() {
        StrBuilder sb = new StrBuilder(500);
        int i = 0;
        MemMap memMap = new MemMap();
        while (true) {
            MemMapEntry entry = memMap.Next();
            if (entry == null) {
                break;
            }

            sb.Append("Entry #")
                    .Append(i).Append(":")
                    .Append(entry.IsFree() ? "free" : "reserved")
                    .AppendLine()
                    .Append("    ")
                    .Append("Base: ")
                    .Append(entry.Base, 10)
                    .AppendLine()
                    .Append("    ")
                    .Append("Length: ")
                    .Append(entry.Length, 10)
                    .AppendLine()
                    .AppendLine();
            i++;
        }
        return sb.toString();
    }

    @Override
    public boolean NeedsRedraw() {
        return _needsRedraw;
    }
}
