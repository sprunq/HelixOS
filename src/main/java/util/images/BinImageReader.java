package util.images;

import kernel.Kernel;

public class BinImageReader {
    public static int[][] decode_data(String data) {

        int width = get_width(data);
        int height = get_height(data);
        int[][] pixel_data = getPixels(data, width, height);
        return pixel_data;
    }

    public static int get_width(String data) {
        int pos = 0;
        int width = (data.get(pos) & 0xFF) << 24
                | (data.get(pos + 1) & 0xFF) << 16
                | (data.get(pos + 2) & 0xFF) << 8
                | (data.get(pos + 3) & 0xFF);
        return width;
    }

    public static int get_height(String data) {
        int pos = 4;
        int height = (data.get(pos) & 0xFF) << 24
                | (data.get(pos + 1) & 0xFF) << 16
                | (data.get(pos + 2) & 0xFF) << 8
                | (data.get(pos + 3) & 0xFF);
        return height;
    }

    public static int[][] getPixels(String data, int width, int height) {
        int pos = 8;
        int[][] pixel_data = new int[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j += 3) {
                int r = Integer.ubyte(data.get(pos));
                int g = Integer.ubyte(data.get(pos + 1));
                int b = Integer.ubyte(data.get(pos + 2));
                int col = Kernel.Display.rgb(r, g, b);
                pixel_data[i][j] = col;
            }
        }
        return pixel_data;
    }
}
