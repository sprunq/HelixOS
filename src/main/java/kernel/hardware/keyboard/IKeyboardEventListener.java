package kernel.hardware.keyboard;

public interface IKeyboardEventListener {
    boolean OnKeyPressed(char keyCode);

    boolean OnKeyReleased(char keyCode);

    String Name();

    int Priority();
}
