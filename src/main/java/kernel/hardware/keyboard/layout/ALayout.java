package kernel.hardware.keyboard.layout;

public abstract class ALayout {
    public abstract int logicalKey(int physicalKey, boolean shift, boolean alt);
}
