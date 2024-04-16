package gui;

import kernel.Kernel;
import kernel.display.video.VM13;
import kernel.display.video.font.AFont;

public class TextField implements IUiElement {
    public final int X;
    public final int Y;
    public final int Width;
    public final int Height;
    public final int SpacingBorder;
    public final int SpacingW;
    public final int SpacingH;
    public final int LineLength;
    public final int LineCount;
    private byte[][] _characters;
    private int[][] _characterColors;
    private int _cursorX;
    private int _cursorY;
    private AFont _font;
    private int _backGroundColor;
    private int _brush;

    public TextField(
            int x,
            int y,
            int width,
            int height,
            int borderSpacing,
            int charSpacing,
            int lineSpacing,
            int backGroundColor,
            int defaultBrushColor,
            AFont font) {
        _cursorX = 0;
        _cursorY = 0;
        _backGroundColor = backGroundColor;
        _font = font;
        X = x;
        Y = y;
        Width = width;
        Height = height;
        SpacingBorder = borderSpacing;
        SpacingW = charSpacing + font.getSpacingW();
        SpacingH = lineSpacing + font.getSpacingH();
        LineLength = (width - borderSpacing * 2) / (font.getWidth() + SpacingW);
        LineCount = (height - borderSpacing * 2) / (font.getHeight() + SpacingH);
        _characters = new byte[LineCount][LineLength];
        _characterColors = new int[LineCount][LineLength];
        _brush = defaultBrushColor;

        Kernel.Vesa.fillrect(X, Y, Width, Height, _backGroundColor);
    }

    public void draw() {

        for (int i = 0; i < LineCount; i++) {
            for (int j = 0; j < LineLength; j++) {
                int x = this.X + j * (_font.getWidth() + SpacingW) + SpacingBorder;
                int y = this.Y + i * (_font.getHeight() + SpacingH) + SpacingBorder;
                int character = _characters[i][j];
                int characterColor = _characterColors[i][j];
                Kernel.Vesa.putCh(character, x, y, _font, characterColor, _backGroundColor);
            }
        }
    }

    public void setCursor(int x, int y) {
        this._cursorX = x;
        this._cursorY = y;
    }

    public void setBrushColor(int color) {
        this._brush = color;
    }

    public void addChar(byte c) {
        if (_cursorX >= LineLength) {
            _cursorX = 0;
            _cursorY++;
        }
        if (_cursorY >= LineCount) {
            scroll();
            _cursorY--;
        }
        _characters[_cursorY][_cursorX] = c;
        _characterColors[_cursorY][_cursorX] = _brush;
        _cursorX++;
    }

    public void addString(String s) {
        for (int i = 0; i < s.length(); i++) {
            byte c = (byte) s.get(i);
            if (c == '\n') {
                newLine();
            } else {
                addChar(c);
            }
        }
    }

    public void addStringln(String s) {
        addString(s);
        newLine();
    }

    public void scroll() {
        for (int i = 0; i < LineCount - 1; i++) {
            for (int j = 0; j < LineLength; j++) {
                _characters[i][j] = _characters[i + 1][j];
                _characterColors[i][j] = _characterColors[i + 1][j];
            }
        }
        for (int j = 0; j < LineLength; j++) {
            _characters[LineCount - 1][j] = (byte) ' ';
            _characterColors[LineCount - 1][j] = _backGroundColor;
        }
    }

    public void clearText() {
        for (int i = 0; i < LineCount; i++) {
            for (int j = 0; j < LineLength; j++) {
                _characters[i][j] = (byte) 0;
            }
        }
        setCursor(0, 0);
    }

    public void clearLine(int line) {
        for (int j = 0; j < LineLength; j++) {
            _characters[line][j] = (byte) 0;
        }
    }

    public void newLine() {
        _cursorX = 0;
        _cursorY++;
        if (_cursorY >= LineCount) {
            scroll();
            _cursorY--;
        }
    }
}
