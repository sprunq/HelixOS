package gui.images;

public class Logo25 extends BinImage {
    protected Logo25(byte[] data) {
        super(data);
    }

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.logo_25;

    public static BinImage load() {
        return new Logo25(DATA);
    }
}
