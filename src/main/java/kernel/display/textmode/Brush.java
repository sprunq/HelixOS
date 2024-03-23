package kernel.display.textmode;

public class Brush {
    public byte color;

    public Brush(byte fg, byte bg) {
        setFg(fg);
        setBg(bg);
        setFgBright(false);
        setBgBright(false);
    }

    public Brush(byte fg, byte bg, boolean fgIsBright, boolean bgIsBright) {
        setFg(fg);
        setBg(bg);
        setFgBright(fgIsBright);
        setBgBright(bgIsBright);
    }

    public void setFg(byte fg) {
        this.color = TmColor.setFg(this.color, fg);
    }

    public void setBg(byte bg) {
        this.color = TmColor.setBg(this.color, bg);
    }

    public void setFgBright(boolean isBright) {
        this.color = TmColor.setFgBright(this.color, isBright);
    }

    public void setBgBright(boolean isBright) {
        this.color = TmColor.setBgBright(this.color, isBright);
    }
}
