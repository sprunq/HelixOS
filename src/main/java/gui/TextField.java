package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.IFont;

public class TextField implements IUiElement {

    public int lineLength;
    public int lines;
    public int spacingW;
    public int spacingH;
    public int x;
    public int y;
    public int width;
    public int height;
    private byte[][] characters;
    private byte[][] characterColors;
    private int cursorX;
    private int cursorY;
    private IFont font;
    private byte backGroundColor;
    private byte brush;

    public TextField(int x, int y, int width, int height, IFont font,
            int charSpacing, int lineSpacing, byte backGroundColor, byte defaultBrushColor) {
        this.x = x;
        this.y = y;
        this.cursorX = 0;
        this.cursorY = 0;
        this.backGroundColor = backGroundColor;
        this.width = width;
        this.height = height;
        this.font = font;
        this.spacingW = charSpacing + font.getSpacingW();
        this.spacingH = lineSpacing + font.getSpacingH();
        this.lineLength = width / (font.getWidth() + spacingW);
        this.lines = height / (font.getHeight() + spacingH);
        this.characters = new byte[lines][lineLength];
        this.characterColors = new byte[lines][lineLength];
        this.brush = defaultBrushColor;
    }

    public void clearDrawing() {
        VM13.setRegion(x, y, width, height, backGroundColor);
    }

    public void draw() {
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < lineLength; j++) {
                int x = this.x + j * (font.getWidth() + spacingW);
                int y = this.y + i * (font.getHeight() + spacingH);
                byte character = characters[i][j];
                byte characterColor = characterColors[i][j];
                VM13.putChar(character, x, y, font, characterColor);
            }
        }
    }

    public void setCursor(int x, int y) {
        this.cursorX = x;
        this.cursorY = y;
    }

    public void setBrushColor(byte color) {
        this.brush = color;
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
        characterColors[cursorY][cursorX] = brush;
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
                characterColors[i][j] = characterColors[i + 1][j];
            }
        }
        for (int j = 0; j < lineLength; j++) {
            characters[lines - 1][j] = (byte) ' ';
            characterColors[lines - 1][j] = backGroundColor;
        }
    }

    public void clearText() {
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < lineLength; j++) {
                characters[i][j] = (byte) 0;
            }
        }
        setCursor(0, 0);
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
