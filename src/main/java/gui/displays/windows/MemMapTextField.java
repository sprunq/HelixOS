package gui.displays.windows;

import formats.fonts.AFont;
import gui.components.TextField;
import kernel.Kernel;
import kernel.bios.call.MemMap;
import kernel.bios.call.MemMapEntry;
import kernel.display.ADisplay;
import util.StrBuilder;

public class MemMapTextField extends AWindow {
    public MemMapTextField(
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
        int bg = Kernel.Display.rgb(100, 100, 100);
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
        _memmapText = getMemMapStr();
        _needsRedraw = true;
    }

    private TextField _textField;
    private boolean _needsRedraw;
    private String _memmapText;

    public void DrawContent(ADisplay display) {
        _textField.clearText();
        _textField.write(_memmapText);
        _textField.draw(display);
        _needsRedraw = false;
    }

    private String getMemMapStr() {
        StrBuilder sb = new StrBuilder(500);
        int contIndex = 0;
        int i = 0;
        do {
            MemMapEntry entry = MemMap.memMap(contIndex);
            contIndex = MemMap.getMemMapContinuationIndex();
            sb
                    .append("Entry #")
                    .append(i).append(":")
                    .append(entry.isFree() ? "free" : "reserved")
                    .appendLine()
                    .append("    ")
                    .append("Base: ")
                    .append(entry.Base, 10)
                    .appendLine()
                    .append("    ")
                    .append("Length: ")
                    .append(entry.Length, 10)
                    .appendLine()
                    .appendLine();
            i++;
        } while (contIndex != 0);
        return sb.toString();
    }

    @Override
    public boolean needsRedraw() {
        return _needsRedraw;
    }
}
