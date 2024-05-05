package gui.displays.windows;

import formats.fonts.AFont;
import gui.Window;
import gui.components.TextField;
import kernel.Kernel;
import kernel.display.GraphicsContext;
import kernel.hardware.keyboard.Key;

public class Editor extends Window {

    private TextField _textField;

    public Editor(
            String title,
            int x,
            int y,
            int z,
            int width,
            int height,
            int border,
            int charSpacing,
            int lineSpacing,
            AFont font) {
        super(x, y, z, width, height, title);

        int bg = Kernel.Display.Rgb(20, 20, 20);
        int fg = Kernel.Display.Rgb(255, 255, 255);
        _textField = new TextField(
                ContentX,
                ContentY,
                z,
                ContentWidth,
                ContentHeight,
                border,
                charSpacing,
                lineSpacing,
                fg,
                bg,
                true,
                font);
    }

    public void DrawContent(GraphicsContext ctx) {
        _textField.Draw(ctx);
    }

    @Override
    public boolean NeedsRedraw() {
        return _textField.NeedsRedraw() || super.NeedsRedraw();
    }

    @Override
    public void OnKeyPressed(char key) {
        switch (key) {
            case '\n':
                _textField.NewLine();
                break;
            case '\b':
                _textField.Backspace();
                break;
            case Key.ARROW_UP:
                MoveCursorUp();
                break;
            case Key.ARROW_DOWN:
                MoveCursorDown();
                break;
            case Key.ARROW_LEFT:
                MoveCursorLeft();
                break;
            case Key.ARROW_RIGHT:
                MoveCursorRight();
                break;
            default:
                if (Key.Ascii(key) != 0) {
                    _textField.Write((byte) key);
                }
                break;
        }
    }

    @Override
    public void OnKeyReleased(char key) {
        _textField.OnKeyReleased(key);
    }

    @Override
    public void LeftClickAt(int x, int y) {
        _textField.SetCursor(ScreenXToCursorX(x), ScreenYToCursorY(y));
    }

    private int ScreenXToCursorX(int x) {
        int ll = _textField.LineLength;
        int lperCell = _textField.Font.Width() + _textField.SpacingW;
        int cell = (x - _textField.X) / lperCell;
        return Math.Clamp(cell, 0, ll - 1);
    }

    private int ScreenYToCursorY(int y) {
        int lperCell = _textField.Font.Height() + _textField.SpacingH;
        int cell = (y - _textField.Y) / lperCell;
        return Math.Clamp(cell, 0, _textField.LineCount - 1);
    }

    private void MoveCursorUp() {
        int x = _textField.GetCursorX();
        int y = _textField.GetCursorY();
        if (y > 0) {
            _textField.SetCursor(x, y - 1);
        }
    }

    private void MoveCursorDown() {
        int x = _textField.GetCursorX();
        int y = _textField.GetCursorY();
        if (y < _textField.LineCount - 1) {
            _textField.SetCursor(x, y + 1);
        }
    }

    private void MoveCursorLeft() {
        int x = _textField.GetCursorX();
        int y = _textField.GetCursorY();
        if (x > 0) {
            _textField.SetCursor(x - 1, y);
        }
    }

    private void MoveCursorRight() {
        int x = _textField.GetCursorX();
        int y = _textField.GetCursorY();
        if (x < _textField.LineLength - 1) {
            _textField.SetCursor(x + 1, y);
        }
    }
}
