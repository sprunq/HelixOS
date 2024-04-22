package gui.images;

import formats.images.QOI.QOIDecoder;
import formats.images.QOI.QOIImage;

public class Logo25 extends QOIImage {

    @SuppressWarnings("static-access")
    public static final byte[] DATA = binimp.ByteData.logo_25_qoi;

    protected Logo25(int width, int height, int channels, int colorSpace, int[][] pixelData) {
        super(width, height, channels, colorSpace, pixelData);
    }

    public static QOIImage load() {
        return QOIDecoder.decode(DATA, 3);
    }
}
