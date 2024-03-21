package kernel.video;

/// Bit 76543210
///     ||||||||
///     |||||^^^-fore colour
///     ||||^----fore colour bright bit
///     |^^^-----back colour
//      ^--------back colour bright bit OR enables blinking Text.
///
/// Blinking can be set via https://www.reddit.com/r/osdev/comments/70fcig/blinking_text/?rdt=51833
public class VidColor {
    public static final byte BLACK = 0;
    public static final byte BLUE = 1;
    public static final byte GREEN = 2;
    public static final byte TURQUOISE = 3;
    public static final byte RED = 4;
    public static final byte VIOLET = 5;
    public static final byte BROWN = 6;
    public static final byte GREY = 7;

    private static final byte BIT_BRIGHTNESS_FG = 3;
    private static final byte BIT_BRIGHTNESS_BG = 7;

    @SJC.Inline
    public static byte set(byte fg, byte bg) {
        return (byte) ((bg << 4) | fg);
    }

    @SJC.Inline
    public static byte setFg(byte color, byte fg) {
        byte colorBits = (byte) (fg & 0x7);
        color &= 0xF8; // 01110000
        color |= colorBits;
        return color;
    }

    @SJC.Inline
    public static byte setBg(byte color, byte bg) {
        byte colorBits = (byte) (bg & 0x7);
        color &= 0x8F; // 10001111
        color |= colorBits << 4;
        return color;
    }

    @SJC.Inline
    public static byte setBrightBg(byte c, boolean isBright) {
        return bitSetTo(c, BIT_BRIGHTNESS_BG, isBright);
    }

    @SJC.Inline
    public static byte setBrightFg(byte c, boolean isBright) {
        return bitSetTo(c, BIT_BRIGHTNESS_FG, isBright);
    }

    @SJC.Inline
    private static byte bitSetTo(byte number, byte n, boolean x) {
        return (byte) ((byte) (number & ~((byte) 1 << n)) | ((byte) (x ? 1 : 0) << n));
    }
}
