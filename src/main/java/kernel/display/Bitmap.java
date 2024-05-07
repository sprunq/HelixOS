package kernel.display;

public class Bitmap {
    public int Width;
    public int Height;
    public int[] PixelData;
    public boolean IsTransparent;

    public Bitmap(int width, int height, int[] pixelData) {
        Width = width;
        Height = height;
        PixelData = pixelData;
        IsTransparent = AnyTransparency();
    }

    @SJC.Inline
    public int GetPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= Width || y >= Height) {
            return 0;
        }

        return PixelData[x + y * Width];
    }

    @SJC.Inline
    public void SetPixel(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= Width || y >= Height) {
            return;
        }

        PixelData[x + y * Width] = color;
    }

    @SJC.Inline
    public int Row(int y) {
        return y * Width;
    }

    @SJC.Inline
    public int Index(int x, int y) {
        return x + y * Width;
    }

    public boolean AnyTransparency() {
        for (int i = 0; i < PixelData.length; i++) {
            if ((PixelData[i] & 0xFF000000) != 0) {
                return true;
            }
        }
        return false;
    }
}
