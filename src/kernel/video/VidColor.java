package kernel.video;

public class VidColor {
    public static final byte BLACK = 0;
    public static final byte BLUE = 1;
    public static final byte GREEN = 2;
    public static final byte TURQUOISE = 3;
    public static final byte RED = 4;
    public static final byte VIOLET = 5;
    public static final byte BROWN = 6;
    public static final byte GREY = 7;

    @SJC.Inline
    public static byte withBg(int foregroundColor, int backgroundColor) {
        return (byte) ((backgroundColor << 4) | foregroundColor);
    }
}
