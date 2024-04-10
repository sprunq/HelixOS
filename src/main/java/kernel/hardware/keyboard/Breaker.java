package kernel.hardware.keyboard;

import assembler.x86;

/*
 * Halts the system when the user presses Ctrl + Alt.
 */
public class Breaker implements IKeyboardEventListener {
    private boolean ctrlDown = false;
    private boolean altDown = false;

    @Override
    public boolean onKeyPressed(char keyCode) {
        switch ((int) keyCode) {
            case Key.LALT:
                altDown = true;
                break;
            case Key.LCTRL:
                ctrlDown = true;
                break;
        }
        if (ctrlDown && altDown) {
            x86.breakpoint();
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        switch ((int) keyCode) {
            case Key.LALT:
                altDown = false;
                return false;
            case Key.LCTRL:
                ctrlDown = false;
                return false;
            default:
                return false;
        }
    }
}
