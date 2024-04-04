package gui;

import kernel.display.videomode.IFont;
import kernel.display.videomode.VidWriter;

public class TextField {

    private byte[][] characters;
    private int lineLength;
    private int lines;
    private int charSpacing;
    private int lineSpacing;
    private int x;
    private int y;
    private int width;
    private int height;
    private int cursorX;
    private int cursorY;
    private IFont font;

    public TextField(int x, int y, int width, int height, IFont font, int charSpacing, int lineSpacing) {
        this.x = x;
        this.y = y;
        this.cursorX = 0;
        this.cursorY = 0;
        this.width = width;
        this.height = height;
        this.font = font;
        this.charSpacing = charSpacing;
        this.lineSpacing = lineSpacing;
        this.lineLength = width / (font.getWidth() + charSpacing);
        this.lines = height / (font.getHeight() + lineSpacing);
        this.characters = new byte[lines][lineLength];
    }

    public void draw() {
        VidWriter.setRegion(x, y, width, height, (byte) 0);
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < lineLength; j++) {
                int x = this.x + j * (font.getWidth() + charSpacing);
                int y = this.y + i * (font.getHeight() + lineSpacing);
                VidWriter.putChar(characters[i][j], font, x, y, (byte) 90);
            }
        }
    }

    public void setCursor(int x, int y) {
        this.cursorX = x;
        this.cursorY = y;
    }

    public void addChar(byte c) {
        if (cursorX >= lineLength) {
            cursorX = 0;
            cursorY++;
        }
        if (cursorY >= lines) {
            scroll();
            cursorY--;
        }
        characters[cursorY][cursorX] = c;
        cursorX++;
    }

    public void addString(String s) {
        for (int i = 0; i < s.length(); i++) {
            addChar((byte) s.charAt(i));
        }
    }

    public void scroll() {
        for (int i = 0; i < lines - 1; i++) {
            for (int j = 0; j < lineLength; j++) {
                characters[i][j] = characters[i + 1][j];
            }
        }
        for (int j = 0; j < lineLength; j++) {
            characters[lines - 1][j] = (byte) ' ';
        }
    }

    public void clear() {
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < lineLength; j++) {
                characters[i][j] = (byte) ' ';
            }
        }
    }

    public void newLine() {
        cursorX = 0;
        cursorY++;
        if (cursorY >= lines) {
            scroll();
            cursorY--;
        }
    }
}
