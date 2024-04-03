package kernel.display.videomode;

public class BasicAsciiFont8x8 {
    public static String FONT = MAGIC.getNamedString("font_ascii_8x8.bim");

    public static final int FONT_WIDTH = 8;
    public static final int FONT_HEIGHT = 8;
    public static final int FONT_CHARACHTERS = 128;

    @SJC.Inline
    public static byte getByte(int ch, int offset) {
        int b = FONT.charAt(ch * FONT_HEIGHT + offset);
        return (byte) b;
    }
}