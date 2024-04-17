package util.images;

public class LogoText extends BinImage {
    protected LogoText(byte[] data) {
        super(data);
    }

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.logo_text;

    public static BinImage load() {
        return new LogoText(DATA);
    }
}
