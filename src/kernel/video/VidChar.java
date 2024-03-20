package kernel.video;

public class VidChar extends STRUCT {
    public byte ch;

    @SJC.Inline
    public void setChar(byte ascii_char) {
        this.ch = ascii_char;
    }
}
