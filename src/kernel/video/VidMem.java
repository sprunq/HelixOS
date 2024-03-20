package kernel.video;

public class VidMem extends STRUCT {
    @SJC(count = 2000)
    public VidCell[] cells;
}