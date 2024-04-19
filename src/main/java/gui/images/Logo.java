package gui.images;

public class Logo extends BinImage {
    protected Logo(byte[] data) {
        super(data);
    }

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.logo;

    public static BinImage load() {
        return new Logo(DATA);
    }
}
