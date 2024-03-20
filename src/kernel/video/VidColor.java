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
    public static byte set(byte foregroundColor, byte backgroundColor) {
        return (byte) ((backgroundColor << 4) | foregroundColor);
    }

    @SJC.Inline
    public static byte foreground(byte fg) {
        return updateForeground((byte) 0, fg);
    }

    public static byte updateForeground(byte color, byte fg) {
        byte colorBits = (byte) (fg & 15);
        color &= 0xF0;
        color |= colorBits;
        return color;
    }

    @SJC.Inline
    public static byte background(byte bg) {
        return updateBackground((byte) 0, bg);
    }

    public static byte updateBackground(byte color, byte bg) {
        byte colorBits = (byte) (bg & 15);
        color &= 0x0F;
        color |= colorBits << 4;
        return color;
    }
}
