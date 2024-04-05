package kernel.display.video.font;

/*
 * Bitmap from:
 * https://github.com/BaronWilliams/Vertical-Fonts/blob/master/font5x7.c
 */
public class Font5x7 implements IFont {
    public static String FONT = MAGIC.getNamedString("font_5x7.bim");

    public static final int FONT_WIDTH = 5;
    public static final int FONT_HEIGHT = 7;
    public static final int FONT_CHARACHTERS_START = 32;
    public static final int FONT_CHARACHTERS_LAST = 126;
    public static final int FONT_CHARACHTERS = FONT_CHARACHTERS_LAST - FONT_CHARACHTERS_START + 1;
    public static final int BYTES_PER_CHAR = 5;

    public static final Font5x7 Instance = new Font5x7();

    public byte getCharacterBitmapLine(int ch, int offset) {
        if (ch < FONT_CHARACHTERS_START || ch > FONT_CHARACHTERS_LAST) {
            return 0;
        }
        if (offset >= FONT_HEIGHT) {
            return 0;
        }
        ch -= FONT_CHARACHTERS_START;
        int b = FONT.charAt(ch * BYTES_PER_CHAR + offset);
        return (byte) b;
    }

    @Override
    public int getWidth() {
        return FONT_HEIGHT;
    }

    @Override
    public int getHeight() {
        return FONT_WIDTH;
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    @Override
    public int getSpacingW() {
        return 0;
    }

    @Override
    public int getSpacingH() {
        return 3;
    }
}