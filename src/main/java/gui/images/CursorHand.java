package gui.images;

import formats.images.BinImage.BinImage;

public class CursorHand extends BinImage {

    protected CursorHand(byte[] data) {
        super(data);
    }

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.cursor_hand_binimg;

    public static BinImage Load() {
        return new CursorHand(DATA);
    }
}
