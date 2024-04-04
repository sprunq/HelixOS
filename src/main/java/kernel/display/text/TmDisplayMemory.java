package kernel.display.text;

public class TmDisplayMemory extends STRUCT {
    @SJC(count = 2000)
    public TmDisplayCell[] cells;
}