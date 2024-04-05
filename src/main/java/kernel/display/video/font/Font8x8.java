package kernel.display.video.font;

public class Font8x8 implements IFont {
    public static String FONT = MAGIC.getNamedString("font_8x8.bim");

    public static final int FONT_WIDTH = 8;
    public static final int FONT_HEIGHT = 8;
    public static final int FONT_CHARACHTERS = 128;

    public static final Font8x8 Instance = new Font8x8();

    @Override
    public byte getCharacterBitmapLine(int ch, int offset) {
        if (offset > FONT_HEIGHT || ch > FONT_CHARACHTERS) {
            return 0;
        }
        int b = FONT.charAt(ch * FONT_HEIGHT + offset);
        return (byte) b;
    }

    @Override
    public int getWidth() {
        return FONT_WIDTH;
    }

    @Override
    public int getHeight() {
        return FONT_HEIGHT;
    }

    @Override
    public boolean isVertical() {
        return false;
    }

    @Override
    public int getSpacingW() {
        return 1;
    }

    @Override
    public int getSpacingH() {
        return 0;
    }
}