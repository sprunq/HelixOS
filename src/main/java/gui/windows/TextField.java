package gui.windows;

import kernel.Kernel;
import kernel.display.ADisplay;
import kernel.display.video.font.AFont;
import kernel.hardware.keyboard.Key;

public class TextField extends AWindow {

    public final int SpacingBorder;
    public final int SpacingW;
    public final int SpacingH;
    public final int LineLength;
    public final int LineCount;

    protected int _cursorX;
    protected int _cursorY;

    protected int _bg;
    protected int _fg;

    protected byte[][] _characters;
    protected int[][] _characterColors;

    protected AFont _font;

    public TextField(
            int x,
            int y,
            int z,
            int width,
            int height,
            int borderSpacing,
            int charSpacing,
            int lineSpacing,
            int fg,
            int bg,
            AFont font) {
        super(x, y, z, width, height);

        _cursorX = 0;
        _cursorY = 0;
        _fg = fg;
        _bg = bg;
        _font = font;
        SpacingBorder = borderSpacing;
        SpacingW = charSpacing + font.getSpacingW();
        SpacingH = lineSpacing + font.getSpacingH();
        LineLength = (width - borderSpacing * 2) / (font.getWidth() + SpacingW);
        LineCount = (height - borderSpacing * 2) / (font.getHeight() + SpacingH);
        _characters = new byte[LineCount][LineLength];
        _characterColors = new int[LineCount][LineLength];
    }

    public void setCursor(int x, int y) {
        this._cursorX = x;
        this._cursorY = y;
    }

    public void setBrushColor(int color) {
        this._fg = color;
    }

    public void write(byte c) {
        if (_cursorX >= LineLength) {
            carriageReturn();
        }
        if (_cursorY >= LineCount) {
            newLine();
        }
        _characters[_cursorY][_cursorX] = c;
        _characterColors[_cursorY][_cursorX] = _fg;
        _cursorX++;
    }

    @SJC.Inline
    public void carriageReturn() {
        _cursorX = 0;
        _cursorY++;
    }

    public void newLine() {
        carriageReturn();
        if (_cursorY >= LineCount) {
            scroll();
            _cursorY--;
        }
    }

    public void write(String s) {
        for (int i = 0; i < s.length(); i++) {
            byte c = (byte) s.get(i);
            if (c == '\n') {
                newLine();
            } else {
                write(c);
            }
        }
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

    @Override
    public void draw(ADisplay display) {
        Kernel.Display.fillrect(X, Y, Width, Height, _bg);

        int xFactor = _font.getWidth() + SpacingW;
        int yFactor = _font.getHeight() + SpacingH;
        int xOffset = this.X + SpacingBorder;
        int yOffset = this.Y + SpacingBorder;
        for (int i = 0; i < LineCount; i++) {
            for (int j = 0; j < LineLength; j++) {
                int character = _characters[i][j];
                int characterColor = _characterColors[i][j];
                // Skip rendering if the character is not visible
                if (characterColor == _bg || Key.ascii(character) == 0) {
                    continue;
                }
                int x = xOffset + j * xFactor;
                int y = yOffset + i * yFactor;
                _font.renderToDisplay(display, x, y, character, characterColor, _bg);
            }
        }
    }

    @Override
    public boolean needsRedraw() {
        return true;
    }
}
