package kernel.display.videomode;

public class Font {

    public static String FONT = MAGIC.getNamedString("font8x8.bim");

    public static final int FONT_WIDTH = 8;
    public static final int FONT_HEIGHT = 8;

    public static byte getByte(int ch, int offset) {
        int b = FONT.charAt(ch * FONT_HEIGHT + offset);
        return (byte) b;
    }
}