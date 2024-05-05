package kernel.hardware.mouse;

public class MouseStatus {
    public boolean LeftButton;
    public boolean RightButton;
    public boolean MiddleButton;
    public boolean Always1;
    public boolean XSign;
    public boolean YSign;
    public boolean XOverflow;
    public boolean YOverflow;

    public void Clear() {
        LeftButton = false;
        RightButton = false;
        MiddleButton = false;
        Always1 = false;
        XSign = false;
        YSign = false;
        XOverflow = false;
        YOverflow = false;
    }
}
