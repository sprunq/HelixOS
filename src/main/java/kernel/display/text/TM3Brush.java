package kernel.display.text;

public class TM3Brush {
    private byte color;

    public TM3Brush() {
        setFg(TM3Color.GREY);
        setBg(TM3Color.BLACK);
    }

    public TM3Brush(byte fg, byte bg) {
        setFg(fg);
        setBg(bg);
    }

    @SJC.Inline
    public void set(byte fg, byte bg) {
        setFg(fg);
        setBg(bg);
    }

    @SJC.Inline
    public void setFg(byte fg) {
        this.color = TM3Color.setFg(this.color, fg);
    }

    @SJC.Inline
    public void setBg(byte bg) {
        this.color = TM3Color.setBg(this.color, bg);
    }

    @SJC.Inline
    public void setFgBright(boolean isBright) {
        this.color = TM3Color.setFgBright(this.color, isBright);
    }

    @SJC.Inline
    public void setBgBright(boolean isBright) {
        this.color = TM3Color.setBgBright(this.color, isBright);
    }

    @SJC.Inline
    public byte getColor() {
        return color;
    }

    @SJC.Inline
    public void setColor(byte color) {
        this.color = color;
    }
}