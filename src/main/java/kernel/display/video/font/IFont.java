package kernel.display.video.font;

public interface IFont {

    public int getWidth();

    public int getHeight();

    public byte getCharacterBitmapLine(int ch, int line);

    public int correctTranslationX(int positionX, int positionY, int relX, int relY);

    public int correctTranslationY(int positionX, int positionY, int relX, int relY);
}
