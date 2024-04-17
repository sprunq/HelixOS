package util.images;

import kernel.Kernel;

public abstract class BinImage {
    public final int Width;
    public final int Height;
    public final int[][] PixelData;

    protected BinImage(byte[] data) {
        int width = get_width(data);
        int height = get_height(data);
        int[][] pixelData = decode_data(data);
        this.Width = width;
        this.Height = height;
        this.PixelData = pixelData;
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
