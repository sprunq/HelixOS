package kernel.display.video.font;

public class Ascii8x8 implements IFont {
    public static String FONT = MAGIC.getNamedString("font_ascii_8x8.bim");

    public static final int FONT_WIDTH = 8;
    public static final int FONT_HEIGHT = 8;
    public static final int FONT_CHARACHTERS = 128;

    private static Ascii8x8 instance;

    public static Ascii8x8 getInstance() {
        if (instance == null) {
            instance = new Ascii8x8();
        }
        return instance;
    }

    public byte getCharacterBitmapLine(int ch, int offset) {
        int b = FONT.charAt(ch * FONT_HEIGHT + offset);
        return (byte) b;
    }

    public int correctTranslationX(int positionX, int positionY, int relX, int relY) {
        int x = positionX + relX;
        return x;
    }

    public int correctTranslationY(int positionX, int positionY, int relX, int relY) {
        int y = positionY + relY;
        return y;
    }

    @Override
    public int getWidth() {
        return FONT_WIDTH;
    }

    @Override
    public int getHeight() {
        return FONT_HEIGHT;
    }
}