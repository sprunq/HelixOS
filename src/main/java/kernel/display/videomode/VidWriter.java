package kernel.display.videomode;

import kernel.Env;
import util.BitHelper;

public class VidWriter {
    private static final VidDisplayMemory vidMem = (VidDisplayMemory) MAGIC.cast2Struct(Env.VGA_VID_BUFFER);

    public static final int WIDTH = 320;
    public static final int HEIGHT = 200;

    public VidWriter() {
    }

    @SJC.Inline
    public static void putPixel(int x, int y, byte color) {
        vidMem.color[offset(x, y)] = color;
    }

    @SJC.Inline
    public static int offset(int x, int y) {
        return WIDTH * y + x;
    }

    public static void font_char(byte c, int x, int y, byte color) {
        for (int yy = 0; yy < 8; yy++) {
            for (int xx = 0; xx < 8; xx++) {
                byte b = Font.getByte(c, yy);
                if (BitHelper.getFlag(b, xx)) {
                    putPixel(x + xx, y + yy, color);
                }
            }
        }
    }
}
