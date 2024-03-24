package kernel.display.textmode;

public class Brush {
    private byte color;

    public Brush() {
        setFg(TmColor.GREY);
        setBg(TmColor.BLACK);
    }

    public Brush(byte fg, byte bg) {
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
        this.color = TmColor.setFg(this.color, fg);
    }

    @SJC.Inline
    public void setBg(byte bg) {
        this.color = TmColor.setBg(this.color, bg);
    }

    @SJC.Inline
    public void setFgBright(boolean isBright) {
        this.color = TmColor.setFgBright(this.color, isBright);
    }

    @SJC.Inline
    public void setBgBright(boolean isBright) {
        this.color = TmColor.setBgBright(this.color, isBright);
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