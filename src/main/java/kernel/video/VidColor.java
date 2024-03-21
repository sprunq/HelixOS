package kernel.video;

import util.BinaryHelper;
import util.BitHelper;

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

    @SJC.Inline
    @SuppressWarnings("unused")
    public static byte set(byte fg, byte bg) {
        return set(fg, bg, false, false);
    }

    @SJC.Inline
    public static byte set(byte fg, byte bg, boolean fgIsBright, boolean bgIsBright) {
        byte color = 0;
        color = setFg(color, fg);
        color = setBg(color, bg);
        color = setBrightFg(color, fgIsBright);
        color = setBrightBg(color, bgIsBright);
        return color;
    }

    @SJC.Inline
    public static byte setFg(byte color, byte fg) {
        return (byte)BitHelper.setRange(color, 0,3, fg);
    }

    @SJC.Inline
    public static byte setBg(byte color, byte bg) {
        return (byte)BitHelper.setRange(color, 4,3, bg);
    }

    @SJC.Inline
    public static byte setBrightBg(byte c, boolean isBright) {
        return (byte)BitHelper.setFlag(c, 7, isBright);
    }

    @SJC.Inline
    public static byte setBrightFg(byte c, boolean isBright) {
        return (byte)BitHelper.setFlag(c, 3, isBright);
    }
}
