package kernel.hardware.keyboard;

import assembler.x86;

/*
 * Halts the system when the user presses Ctrl + Alt.
 */
public class Breaker implements IKeyboardEventListener {
    private boolean _ctrlDown = false;
    private boolean _altDown = false;

    @Override
    public boolean onKeyPressed(char keyCode) {
        switch ((int) keyCode) {
            case Key.LALT:
                _altDown = true;
                break;
            case Key.LCTRL:
                _ctrlDown = true;
                break;
        }
        if (_ctrlDown && _altDown) {
            x86.breakpoint();
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        switch ((int) keyCode) {
            case Key.LALT:
                _altDown = false;
                return false;
            case Key.LCTRL:
                _ctrlDown = false;
                return false;
            default:
                return false;
        }
    }
}
