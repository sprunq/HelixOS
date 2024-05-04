package gui.displays.windows;

import gui.Window;
import kernel.schedeule.Task;

public class BounceTask extends Task {
    Window window;

    public BounceTask(Window window) {
        super("BounceTask");
        this.window = window;
    }

    @Override
    public boolean WantsActive() {
        return true;
    }

    @Override
    public void Run() {
    }
}
