package kernel.display.videomode;

public class VidDisplayMemory extends STRUCT {
    @SJC(count = 64000)
    public byte[] color;
}