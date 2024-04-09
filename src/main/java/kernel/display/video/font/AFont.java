package kernel.display.video.font;

public abstract class AFont {

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getSpacingW();

    public abstract int getSpacingH();

    public abstract byte getCharacterBitmapLine(int ch, int line);

    public abstract boolean isVertical();
}
