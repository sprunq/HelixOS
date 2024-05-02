package kernel.tasks;

import kernel.hardware.keyboard.KeyboardController;
import kernel.schedeule.Task;

public class KeyDistributor extends Task {

    @Override
    public void Run() {
        while (KeyboardController.HasNewEvent()) {
            KeyboardController.ReadEvent();
        }
    }

    @Override
    public boolean WantsActive() {
        return true;
    }
}
