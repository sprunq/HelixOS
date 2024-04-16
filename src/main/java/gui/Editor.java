package gui;

import kernel.display.video.font.AFont;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;

public class Editor extends TextField implements IKeyboardEventListener {
    public Editor(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            int fg,
            int bg) {
        super(x,
                y,
                width,
                height,
                border,
                charSpacing,
                lineSpacing,
                fg,
                bg,
                font);
    }

    @Override
    public boolean onKeyPressed(char keyCode) {
        if (keyCode != 0) {
            switch ((int) keyCode) {
                case Key.ENTER:
                    newLine();
                    break;
                default:
                    int x = Key.ascii(keyCode);
                    if (x != 0) {
                        addChar((byte) x);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        // do nothing
        return false;
    }

    @Override
    public boolean isDirty() {
        return super.isDirty();
    }
}
