package kernel.display.video.font;

/*
 * Bitmap from:
 * https://github.com/BaronWilliams/Vertical-Fonts/blob/master/font3x6.c
 */
public class Font3x6 extends AFont {
    public static final int FONT_WIDTH = 3;
    public static final int FONT_HEIGHT = 6;
    public static final int FONT_CHARACHTERS_START = 32;
    public static final int FONT_CHARACHTERS_LAST = 126;
    public static final int FONT_CHARACHTERS = FONT_CHARACHTERS_LAST - FONT_CHARACHTERS_START + 1;
    public static final int BYTES_PER_CHAR = 3;

    public static final Font3x6 Instance = new Font3x6();

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
        return true;
    }

    @Override
    public int getSpacingW() {
        return 1;
    }

    @Override
    public int getSpacingH() {
        return 0;
    }

    public int getCharacterBitmapLine(int ch, int offset) {
        if (ch < FONT_CHARACHTERS_START || ch > FONT_CHARACHTERS_LAST) {
            return 0;
        }
        if (offset >= FONT_HEIGHT) {
            return 0;
        }
        ch -= FONT_CHARACHTERS_START;
        byte b = FONT_BYTES[ch * BYTES_PER_CHAR + offset];
        return Integer.ubyte(b);
    }

    private static final byte[] FONT_BYTES = {
            0x00, 0x00, 0x00, // ' ' 32
            0x00, 0x17, 0x00, // '!' 33
            0x03, 0x00, 0x03, // '"' 34
            0x1F, 0x0A, 0x1F, // '//' 35
            0x16, 0x1F, 0x0D, // '$' 36
            0x19, 0x04, 0x13, // '%' 37
            0x1A, 0x15, 0x0A, // '&' 38
            0x00, 0x03, 0x00, // ''' 39
            0x0E, 0x11, 0x00, // '(' 40
            0x00, 0x11, 0x0E, // ')' 41
            0x0A, 0x04, 0x0A, // '*' 42
            0x04, 0x0E, 0x04, // '+' 43
            0x10, 0x08, 0x00, // ',' 44
            0x04, 0x04, 0x04, // '-' 45
            0x00, 0x10, 0x00, // '.' 46
            0x18, 0x04, 0x03, // '/' 47
            0x1F, 0x11, 0x1F, // '0' 48
            0x12, 0x1F, 0x10, // '1' 49
            0x1D, 0x15, 0x17, // '2' 50
            0x11, 0x15, 0x1F, // '3' 51
            0x07, 0x04, 0x1F, // '4' 52
            0x17, 0x15, 0x1D, // '5' 53
            0x1F, 0x15, 0x1D, // '6' 54
            0x19, 0x05, 0x03, // '7' 55
            0x1F, 0x15, 0x1F, // '8' 56
            0x17, 0x15, 0x1F, // '9' 57
            0x00, 0x0A, 0x00, // ':' 58
            0x10, 0x0A, 0x00, // ';' 59
            0x04, 0x0A, 0x11, // '<' 60
            0x0A, 0x0A, 0x0A, // '=' 61
            0x11, 0x0A, 0x04, // '>' 62
            0x01, 0x15, 0x03, // '?' 63
            0x0E, 0x11, 0x16, // '@' 64
            0x1F, 0x05, 0x1F, // 'A' 65
            0x1F, 0x15, 0x1B, // 'B' 66
            0x0E, 0x11, 0x11, // 'C' 67
            0x1F, 0x11, 0x0E, // 'D' 68
            0x1F, 0x15, 0x15, // 'E' 69
            0x1F, 0x05, 0x05, // 'F' 70
            0x0E, 0x11, 0x1D, // 'G' 71
            0x1F, 0x04, 0x1F, // 'H' 72
            0x11, 0x1F, 0x11, // 'I' 73
            0x08, 0x11, 0x0F, // 'J' 74
            0x1F, 0x04, 0x1B, // 'K' 75
            0x1F, 0x10, 0x10, // 'L' 76
            0x1F, 0x02, 0x1F, // 'M' 77
            0x1F, 0x01, 0x1E, // 'N' 78
            0x0E, 0x11, 0x0E, // 'O' 79
            0x1F, 0x05, 0x06, // 'P' 80
            0x0E, 0x19, 0x1F, // 'Q' 81
            0x1F, 0x05, 0x1B, // 'R' 82
            0x12, 0x15, 0x09, // 'S' 83
            0x01, 0x1F, 0x01, // 'T' 84
            0x0F, 0x10, 0x1F, // 'U' 85
            0x0F, 0x10, 0x0F, // 'V' 86
            0x1F, 0x0C, 0x1F, // 'W' 87
            0x1B, 0x04, 0x1B, // 'X' 88
            0x03, 0x1C, 0x03, // 'Y' 89
            0x19, 0x15, 0x13, // 'Z' 90
            0x00, 0x1F, 0x11, // '[' 91
            0x03, 0x04, 0x18, // '\' 92
            0x11, 0x1F, 0x00, // ']' 93
            0x02, 0x01, 0x02, // '^' 94
            0x10, 0x10, 0x10, // '_' 95
            0x01, 0x02, 0x00, // '`' 96
            0x0C, 0x12, 0x1E, // 'a' 97
            0x1F, 0x12, 0x0C, // 'b' 98
            0x0C, 0x12, 0x12, // 'c' 99
            0x0C, 0x12, 0x1F, // 'd' 100
            0x0C, 0x1A, 0x16, // 'e' 101
            0x1E, 0x09, 0x02, // 'f' 102
            0x24, 0x2A, 0x1E, // 'g' 103
            0x1F, 0x04, 0x18, // 'h' 104
            0x00, 0x1D, 0x00, // 'i' 105
            0x20, 0x20, 0x1D, // 'j' 106
            0x1F, 0x04, 0x1A, // 'k' 107
            0x00, 0x0F, 0x10, // 'l' 108
            0x1E, 0x04, 0x1E, // 'm' 109
            0x1E, 0x02, 0x1C, // 'n' 110
            0x0C, 0x12, 0x0C, // 'o' 111
            0x3E, 0x0A, 0x04, // 'p' 112
            0x04, 0x0A, 0x3E, // 'q' 113
            0x1E, 0x04, 0x02, // 'r' 114
            0x14, 0x16, 0x1A, // 's' 115
            0x02, 0x0F, 0x12, // 't' 116
            0x0E, 0x10, 0x1E, // 'u' 117
            0x0E, 0x10, 0x0E, // 'v' 118
            0x1E, 0x08, 0x1E, // 'w' 119
            0x1A, 0x04, 0x1A, // 'x' 120
            0x26, 0x28, 0x1E, // 'y' 121
            0x1A, 0x1E, 0x16, // 'z' 122
            0x04, 0x1F, 0x11, // '{' 123
            0x00, 0x1F, 0x00, // '|' 124
            0x11, 0x1F, 0x04, // '}' 125
            0x01, 0x03, 0x02, // '~' 126
            0x0E, 0x09, 0x0E // delete 127
    };
}