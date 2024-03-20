package kernel.video;

public class VidColor extends STRUCT {
    public byte color;

    public static final byte BLACK = 0;
    public static final byte BLUE = 1;
    public static final byte GREEN = 2;
    public static final byte TURQUOISE = 3;
    public static final byte RED = 4;
    public static final byte VIOLET = 5;
    public static final byte BROWN = 6;
    public static final byte GREY = 7;

    @SJC.Inline
    public void setColorFg(byte fg) {
        byte colorBits = (byte) (fg & 15);
        this.color &= 0xF0;
        this.color |= colorBits;
    }

    @SJC.Inline
    public void setColorBg(byte bg) {
        byte colorBits = (byte) (bg & 15);
        this.color &= 0x0F;
        this.color |= colorBits << 4;
    }

    @SJC.Inline
    public void setColor(byte foregroundColor, byte backgroundColor) {
        this.color = (byte) ((backgroundColor << 4) | foregroundColor);
    }

    @SJC.Inline
    public void setColor(byte col) {
        this.color = col;
    }
}
