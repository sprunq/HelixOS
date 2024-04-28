package kernel.hardware.keyboard.layout;

public abstract class ALayout {
    public abstract int LogicalKey(int physicalKey, boolean shift, boolean alt);
}
