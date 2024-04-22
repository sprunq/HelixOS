package formats.images.BinImage;

import formats.images.Image;
import kernel.Kernel;

/*
 * One of the worst possible image formats (self-made).
 * Format:
 * 4 bytes: width
 * 4 bytes: height
 * width * height * 3 bytes: pixel data (RGB)
 */
public abstract class BinImage extends Image {
    protected BinImage(byte[] data) {
        super();
        Width = get_width(data);
        Height = get_height(data);
        PixelData = decode_data(data);
    }

    protected static int[][] decode_data(byte[] data) {
        int width = get_width(data);
        int height = get_height(data);
        int[][] pixel_data = getPixels(data, width, height);
        return pixel_data;
    }

    protected static int get_width(byte[] data) {
        int pos = 0;
        int width = (data[pos] & 0xFF) << 24
                | (data[pos + 1] & 0xFF) << 16
                | (data[pos + 2] & 0xFF) << 8
                | (data[pos + 3] & 0xFF);
        return width;
    }

    protected static int get_height(byte[] data) {
        int pos = 4;
        int height = (data[pos] & 0xFF) << 24
                | (data[pos + 1] & 0xFF) << 16
                | (data[pos + 2] & 0xFF) << 8
                | (data[pos + 3] & 0xFF);
        return height;
    }

    protected static int[][] getPixels(byte[] data, int width, int height) {
        int pos = 8;
        int[][] pixel_data = new int[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = Integer.ubyte(data[pos++]);
                int g = Integer.ubyte(data[pos++]);
                int b = Integer.ubyte(data[pos++]);
                int col = Kernel.Display.rgb(r, g, b);
                pixel_data[j][i] = col;
            }
        }
        return pixel_data;
    }
}
