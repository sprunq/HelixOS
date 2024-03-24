package kernel.memory;

import kernel.display.textmode.TmColor;
import kernel.display.textmode.TmWriter;

public class MemoryView {

    TmWriter writer;

    public MemoryView(TmWriter writer) {
        this.writer = writer;
    }

    public void view(int address) {
        this.writer.clearScreen();
        this.writer.brush.set(TmColor.GREY, TmColor.BLACK);
        for (int i = 0; i < 16; i++) {
            // byte b = MAGIC.rMem8(address + i);
            // char[] hex = ConversionH.itoa(b, 16);

            this.writer.print('?');
        }
    }

}
