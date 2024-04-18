package kernel.display.vm13;

import kernel.MemoryLayout;
import kernel.display.tm3.AFont;
import kernel.memory.Memory;
import util.BitHelper;
import util.MathH;
import util.logging.Logger;

public class VM13 {
    private static final VM13Memory VidMem = (VM13Memory) MAGIC.cast2Struct(MemoryLayout.VGA_VID_BUFFER_START);

    public static final int WIDTH = 320;
    public static final int HEIGHT = 200;
    public static final int SIZE = WIDTH * HEIGHT;

    public static final int PALETTE_MASK = 0x3C6;
    public static final int PALETTE_WRITE = 0x3C8;
    public static final int PALETTE_DATA = 0x3C9;

    /*
     * Back buffer for double buffering.
     * Without double buffering, the screen would flicker.
     */
    private static byte[] _backBuffer = new byte[WIDTH * HEIGHT];

    /*
     * Swaps the back buffer with the video memory.
     */
    public static void swap() {
        Memory.copyBytes(
                MAGIC.addr(_backBuffer[0]),
                MAGIC.addr(VidMem.Color[0]),
                WIDTH * HEIGHT);
    }

    public static void clearBackBuffer() {
        Memory.setBytes(MAGIC.addr(_backBuffer[0]), SIZE, (byte) 0);
    }

    @SJC.Inline
    public static void setPixel(int x, int y, byte color) {
        _backBuffer[offset(x, y)] = color;
    }

    @SJC.Inline
    public static int offset(int x, int y) {
        return WIDTH * y + x;
    }

    public static void putChar(byte c, int x, int y, AFont font, byte color) {
        int fontWidth = font.getWidth();
        int fontHeight = font.getHeight();
        boolean fontVertical = font.isVertical();

        for (int charLine = 0; charLine < fontHeight; charLine++) {
            int b = font.getCharacterBitmapLine(c, charLine);
            for (int lineBit = 0; lineBit < fontWidth; lineBit++) {
                int bit = BitHelper.getBit(b, lineBit);
                if (bit == 1) {
                    int posX = x + charLine;
                    int posY = y + lineBit;
                    if (fontVertical) {
                        posX = x + charLine;
                        posY = y + lineBit;
                    } else {
                        posX = x + lineBit;
                        posY = y + charLine;
                    }
                    setPixel(posX, posY, color);
                }
            }
        }
    }

    public static void fillrect(int x, int y, int width, int height, byte color) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setPixel(x + j, y + i, color);
            }
        }
    }

    public static void clearScreen(byte color) {
        fillrect(0, 0, WIDTH, HEIGHT, color);
        VM13.swap();
    }

    /*
     * Has to be called after activating graphics mode.
     */
    public static void setPalette() {
        MAGIC.wIOs8(PALETTE_MASK, (byte) 0xFF);
        MAGIC.wIOs8(PALETTE_WRITE, (byte) 0);
        for (int i = 0; i < 255; i++) {
            MAGIC.wIOs8(PALETTE_DATA, (byte) ((((i >> 5) & 0x7) * (256 / 8)) / 4));
            MAGIC.wIOs8(PALETTE_DATA, (byte) ((((i >> 2) & 0x7) * (256 / 8)) / 4));
            MAGIC.wIOs8(PALETTE_DATA, (byte) ((((i >> 0) & 0x3) * (256 / 4)) / 4));
        }
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
        MAGIC.wIOs8(PALETTE_DATA, (byte) 0x3F);
        Logger.info("VGA", "Set palette");
    }

    /*
     * Give rgb in the range of 0-255.
     * return the color in the format 0xRRRGGGBB.
     */
    public static byte rgb(int r, int g, int b) {
        int red = MathH.compress(r, 0, 255, 0, 7);
        int green = MathH.compress(g, 0, 255, 0, 7);
        int blue = MathH.compress(b, 0, 255, 0, 3);
        int color = 0;
        color = BitHelper.setRange(color, 0, 2, blue);
        color = BitHelper.setRange(color, 2, 3, green);
        color = BitHelper.setRange(color, 5, 3, red);
        return (byte) color;
    }

    /*
     * Float version of rgb.
     * Give rgb in the range of 0-1.
     * return the color in the format 0xRRRGGGBB.
     */
    public static byte frgb(double r, double g, double b) {
        int red = (int) (7.0 * r);
        int green = (int) (7.0 * g);
        int blue = (int) (3.0 * b);
        int color = 0;
        color = BitHelper.setRange(color, 0, 2, blue);
        color = BitHelper.setRange(color, 2, 3, green);
        color = BitHelper.setRange(color, 5, 3, red);
        return (byte) color;
    }

    /*
     * Direct version of rgb.
     * Give r in the range of 0-7.
     * Give g in the range of 0-7.
     * Give b in the range of 0-3.
     * return the color in the format 0xRRRGGGBB.
     */
    public static byte drgb(int r, int g, int b) {
        int color = 0;
        int red = MathH.clamp(r, 0, 7);
        int green = MathH.clamp(g, 0, 7);
        int blue = MathH.clamp(b, 0, 3);
        color = BitHelper.setRange(color, 0, 2, blue);
        color = BitHelper.setRange(color, 2, 3, green);
        color = BitHelper.setRange(color, 5, 3, red);
        return (byte) color;
    }
}