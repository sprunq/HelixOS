package gui;

import kernel.Kernel;
import kernel.Logger;
import kernel.display.video.font.AFont;

public class TextField implements IUIElement {
    public final int X;
    public final int Y;
    public final int Width;
    public final int Height;
    public final int SpacingBorder;
    public final int SpacingW;
    public final int SpacingH;
    public final int LineLength;
    public final int LineCount;
    protected byte[][] _characters;
    protected int[][] _characterColors;
    protected int _cursorX;
    protected int _cursorY;
    protected AFont _font;
    protected final int _bg;
    protected int _fg;
    protected int[][] _fontBuffer;

    private boolean _dirty;

    public TextField(
            int x,
            int y,
            int width,
            int height,
            int borderSpacing,
            int charSpacing,
            int lineSpacing,
            int fg,
            int bg,
            AFont font) {
        _cursorX = 0;
        _cursorY = 0;
        _fg = fg;
        _bg = bg;
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
        _fontBuffer = new int[font.getHeight()][font.getWidth()];
        _dirty = true;
    }

    public void setCursor(int x, int y) {
        this._cursorX = x;
        this._cursorY = y;
    }

    public void setBrushColor(int color) {
        this._fg = color;
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
        _characterColors[_cursorY][_cursorX] = _fg;
        _cursorX++;
        _dirty = true;
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
            _characterColors[LineCount - 1][j] = _bg;
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

    @Override
    public void drawFg() {
        for (int i = 0; i < LineCount; i++) {
            for (int j = 0; j < LineLength; j++) {
                int x = this.X + j * (_font.getWidth() + SpacingW) + SpacingBorder;
                int y = this.Y + i * (_font.getHeight() + SpacingH) + SpacingBorder;
                int character = _characters[i][j];
                int characterColor = _characterColors[i][j];
                _fontBuffer = _font.renderToBitmap(_fontBuffer, character, characterColor, _bg);
                Kernel.Display.setBitmap(x, y, _fontBuffer);
            }
        }
        _dirty = false;
    }

    @Override
    public boolean isDirty() {
        if (_dirty) {
            Logger.trace("TextField", "Dirty");
        }
        return _dirty;
    }

    @Override
    public void drawBg() {
        Kernel.Display.fillrect(X, Y, Width, Height, _bg);
    }
}
