package kernel.display.video.font;

/*
 * https://github.com/Ameba8195/Arduino/blob/master/hardware_v2/cores/arduino/font5x7.h
 */
public class Ascii7x5 implements IFont {
    public static String FONT = MAGIC.getNamedString("font_ascii_7x5.bim");

    public static final int FONT_WIDTH = 5;
    public static final int FONT_HEIGHT = 7;
    public static final int BYTES_PER_CHAR = 5;

    private static Ascii7x5 instance;

    public static Ascii7x5 getInstance() {
        if (instance == null) {
            instance = new Ascii7x5();
        }
        return instance;
    }

    public byte getCharacterBitmapLine(int ch, int line) {
        if (line >= 5) {
            return 0;
        }
        int b = FONT.charAt(ch * BYTES_PER_CHAR + line);
        return (byte) b;
    }

    public int correctTranslationX(int positionX, int positionY, int relX, int relY) {
        int x = positionX + relY;
        return x;
    }

    public int correctTranslationY(int positionX, int positionY, int relX, int relY) {
        int y = positionY + relX;
        return y;
    }

    /*
     * Font is rotated
     */
    @Override
    public int getWidth() {
        return FONT_HEIGHT;
    }

    /*
     * Font is rotated
     */
    @Override
    public int getHeight() {
        return FONT_WIDTH;
    }
}