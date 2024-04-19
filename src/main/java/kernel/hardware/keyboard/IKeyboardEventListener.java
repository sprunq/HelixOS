package kernel.hardware.keyboard;

public interface IKeyboardEventListener {
    boolean onKeyPressed(char keyCode);

    boolean onKeyReleased(char keyCode);

    String name();
}
