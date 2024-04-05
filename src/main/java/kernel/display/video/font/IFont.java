package kernel.display.video.font;

public interface IFont {

    public int getWidth();

    public int getHeight();

    public int getSpacingW();

    public int getSpacingH();

    public byte getCharacterBitmapLine(int ch, int line);

    public boolean isVertical();
}
