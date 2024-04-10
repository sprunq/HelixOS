package gui;

import kernel.bios.BIOS;
import kernel.bios.MemMapEntry;
import kernel.display.video.VM13;
import kernel.display.video.font.AFont;

public class MemMapTextField extends TextField {
    public MemMapTextField(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            byte backGroundColor) {
        super(x, y, width, height, border, charSpacing, lineSpacing, backGroundColor, VM13.frgb(1.0, 1.0, 1.0), font);
    }

    public void draw() {
        clearText();
        addString("System Memory Map");
        newLine();
        int idx = 0;
        do {
            MemMapEntry entry = BIOS.memMap(idx);

            addString("Entry ");
            addString(Integer.toString(idx, 10));
            addString(entry.type == 1 ? " (free)" : " (reserved)");
            newLine();
            addString("  - BASE: 0x");
            addString(Long.toString(entry.base, 16));
            newLine();
            addString("  - LEN: 0x");
            addString(Long.toString(entry.length, 16));
            newLine();

            idx = BIOS.getMemMapContinuationIndex();
        } while (idx != 0);
        super.draw();
    }
}
