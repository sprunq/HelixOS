package kernel.display;

import kernel.memory.Memory;

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

    public Bitmap(int width, int height, boolean isTransparent) {
        Width = width;
        Height = height;
        IsTransparent = isTransparent;
        PixelData = new int[Width * Height];
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

    public void Rectangle(int x, int y, int width, int height, int color) {
        // only draw visible part
        if (x < 0) {
            width += x;
            x = 0;
        }

        if (y < 0) {
            height += y;
            y = 0;
        }

        if (x + width > Width) {
            width = Height - x;
        }

        if (y + height > Height) {
            height = Height - y;
        }

        // this should not happen but it does and im confused
        // somehow returning fixes it but it makes no sense
        if (width <= 0 || height <= 0) {
            return;
        }

        int addr32 = x + y * Width;
        int addrR32 = MAGIC.addr(PixelData[addr32]);
        for (int i = 0; i < height; i++) {
            Memory.Memset32(addrR32, width, color);
            addrR32 += Width << 2;
        }
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
