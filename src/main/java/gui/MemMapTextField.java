package gui;

import kernel.Kernel;
import kernel.bios.BIOS;
import kernel.bios.MemMapEntry;
import kernel.display.video.font.AFont;
import util.StrBuilder;

public class MemMapTextField extends TextField {

    private static StrBuilder _sb;

    public MemMapTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            int backGroundColor) {
        super(x, y, width, height, border, charSpacing,
                lineSpacing,
                Kernel.Display.rgb(255, 255, 255),
                backGroundColor,
                font);
        _sb = new StrBuilder(512);
    }

    @Override
    public void drawFg() {
        clearText();
        _sb.clearKeepCapacity();
        _sb.appendLine("System Memory Map");
        int idx = 0;
        do {
            MemMapEntry entry = BIOS.memMap(idx);

            _sb.append("Entry ")
                    .append(idx, 10)
                    .appendLine(entry.Type == 1 ? " (free)" : " (reserved)")
                    .append("  - BASE: ")
                    .append("0x")
                    .appendLine(entry.Base, 16)
                    .append("  - LEN: ")
                    .append("0x")
                    .appendLine(entry.Length, 16);

            idx = BIOS.getMemMapContinuationIndex();
        } while (idx != 0);
        addString(_sb.toString());
        super.drawFg();
    }
}
