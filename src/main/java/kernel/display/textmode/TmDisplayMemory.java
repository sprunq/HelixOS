package kernel.display.textmode;

public class TmDisplayMemory extends STRUCT {
    @SJC(count = 2000)
    public TmDisplayCell[] cells;
}