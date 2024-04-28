package gui.images;

import formats.images.QOI.QOIDecoder;
import formats.images.QOI.QOIImage;

public class Logo extends QOIImage {
    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.logo_qoi;

    protected Logo(int width, int height, int channels, int colorSpace, int[][] pixelData) {
        super(width, height, channels, colorSpace, pixelData);
    }

    public static QOIImage Load() {
        return QOIDecoder.Decode(DATA, 3);
    }
}
