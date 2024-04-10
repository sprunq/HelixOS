package kernel.hardware.keyboard;

import assembler.x86;

public class Breaker implements IKeyboardEventListener {
    private boolean ctrlDown = false;
    private boolean altDown = false;

    @Override
    public boolean onKeyPressed(char keyCode) {
        boolean consumed = false;
        switch ((int) keyCode) {
            case Key.LALT:
                altDown = true;
                consumed = true;
                break;
            case Key.LCTRL:
                ctrlDown = true;
                consumed = true;
                break;
        }
        if (ctrlDown && altDown) {
            x86.breakpoint();
        }
        return consumed;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        switch ((int) keyCode) {
            case Key.LALT:
                altDown = false;
                return true;
            case Key.LCTRL:
                ctrlDown = false;
                return true;
            default:
                return false;
        }
    }
}
