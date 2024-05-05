package kernel.hardware.mouse;

import util.IDebug;
import util.StrBuilder;

public class MouseEvent implements IDebug {
    public static final int LEFT_BUTTON = 1;
    public static final int RIGHT_BUTTON = 2;
    public static final int MIDDLE_BUTTON = 4;

    public int X_Delta;
    public int Y_Delta;
    public int ButtonState;

    public boolean LeftButtonPressed() {
        return (ButtonState & LEFT_BUTTON) != 0;
    }

    public boolean RightButtonPressed() {
        return (ButtonState & RIGHT_BUTTON) != 0;
    }

    public boolean MiddleButtonPressed() {
        return (ButtonState & MIDDLE_BUTTON) != 0;
    }

    @Override
    public String Debug() {
        return new StrBuilder(64).Append("MouseEvent{")
                .Append("X_Delta=").Append(X_Delta)
                .Append(", Y_Delta=").Append(Y_Delta)
                .Append(", ButtonState=0b").Append(ButtonState, 2)
                .Append("}").toString();
    }
}
