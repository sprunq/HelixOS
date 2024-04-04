package kernel.display.video.m13;

import kernel.Env;
import kernel.display.video.font.IFont;
import util.BitHelper;

public class VideoMode13 {
    private static final VideoMode13Memory vidMem = (VideoMode13Memory) MAGIC.cast2Struct(Env.VGA_VID_BUFFER);

    public static final int WIDTH = 320;
    public static final int HEIGHT = 200;

    public static final int PALETTE_MASK = 0x3C6;
    public static final int PALETTE_WRITE = 0x3C8;
    public static final int PALETTE_DATA = 0x3C9;

    public VideoMode13() {
    }

    @SJC.Inline
    public static void putPixel(int x, int y, byte color) {
        vidMem.color[offset(x, y)] = color;
    }

    @SJC.Inline
    public static int offset(int x, int y) {
        return WIDTH * y + x;
    }

    public static void putChar(byte c, IFont font, int positionX, int positionY, byte color) {
        for (int charLine = 0; charLine < font.getHeight(); charLine++) {
            byte b = font.getCharacterBitmapLine(c, charLine);
            for (int lineBit = 0; lineBit < font.getWidth(); lineBit++) {
                if (BitHelper.getFlag(b, lineBit)) {
                    int x = font.correctTranslationX(positionX, positionY, lineBit, charLine);
                    int y = font.correctTranslationY(positionX, positionY, lineBit, charLine);
                    putPixel(x, y, color);
                }
            }
        }
    }

    public static void setRegion(int x, int y, int width, int height, byte color) {
        for (int yy = y; yy < y + height; yy++) {
            for (int xx = x; xx < x + width; xx++) {
                putPixel(xx, yy, color);
            }
        }
    }

    /*
     * Does not work :(
     */
    public static void setPalette() {
        MAGIC.wIOs8(PALETTE_MASK, (byte) 0xFF);
        MAGIC.wIOs8(PALETTE_WRITE, (byte) 0);
        for (int i = 0; i < 255; i++) {
            MAGIC.wIOs8(PALETTE_DATA, (byte) 20);
            MAGIC.wIOs8(PALETTE_DATA, (byte) 0);
            MAGIC.wIOs8(PALETTE_DATA, (byte) 20);
        }
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
    }
}