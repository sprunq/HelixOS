package kernel.display.tm3;

import kernel.display.ADisplay;
import util.BitHelper;

public abstract class AFont {

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getSpacingW();

    public abstract int getSpacingH();

    public abstract int getCharacterBitmapLine(int ch, int line);

    public abstract boolean isVertical();

    public void renderToDisplay(ADisplay display, int x, int y, int ch, int color, int backColor) {
        int fontWidth = getWidth();
        int fontHeight = getHeight();
        boolean fontVertical = isVertical();

        for (int charLine = 0; charLine < fontWidth; charLine++) {
            int b = getCharacterBitmapLine(ch, charLine);
            for (int lineBit = 0; lineBit < fontHeight; lineBit++) {
                int bit = BitHelper.getBit(b, lineBit);
                int posX = x;
                int posY = y;
                if (fontVertical) {
                    posX += charLine;
                    posY += lineBit;
                } else {
                    posX += lineBit;
                    posY += charLine;
                }
                if (bit == 1) {
                    display.setPixel(posX, posY, color);
                } else {
                    display.setPixel(posX, posY, backColor);
                }
            }
        }
    }
}
