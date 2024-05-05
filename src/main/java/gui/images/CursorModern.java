package gui.images;

import formats.images.BinImage.BinImage;

public class CursorModern extends BinImage {

    protected CursorModern(byte[] data) {
        super(data);
    }

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.cursor_modern_binimg;

    public static BinImage Load() {
        return new CursorModern(DATA);
    }
}
