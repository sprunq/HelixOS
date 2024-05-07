package kernel.display.vesa;

import kernel.Kernel;
import kernel.bios.call.DisplayModes;
import kernel.display.Bitmap;
import kernel.display.GraphicsContext;
import kernel.memory.Memory;
import kernel.trace.logging.Logger;

public class VESAGraphics extends GraphicsContext {
    public VESAMode curMode;
    private byte[] buffer;
    private boolean needsRedraw;

    public VESAGraphics(VESAMode mode) {
        if (mode == null) {
            Kernel.panic("VESAGraphics.setMode: mode is null");
        }
        if (mode.ColorDepth != 32) {
            Kernel.panic("VESAGraphics.setMode: only 32 bit color depth is supported");
        }
        int size = mode.XRes * mode.YRes * mode.BytesPerColor();
        buffer = new byte[size];
        needsRedraw = true;
        curMode = mode;

        Logger.Info("VESA", "SetMode to ".append(curMode.Debug()));
    }

    @Override
    public void Activate() {
        Logger.Info("VESA", "Activate VESA Graphics Mode");
        DisplayModes.SetVesaMode(curMode.ModeNr);
    }

    @Override
    public int Width() {
        return this.curMode.XRes;
    }

    @Override
    public int Height() {
        return this.curMode.YRes;
    }

    @Override
    public int Rgb(int r, int g, int b) {
        int red, green, blue;
        red = Math.Clamp(r, 0, 255);
        green = Math.Clamp(g, 0, 255);
        blue = Math.Clamp(b, 0, 255);
        return (blue << 0) | (green << 8) | (red << 16) | (255 << 24);
    }

    @Override
    public int Argb(int a, int r, int g, int b) {
        int red, green, blue, alpha;
        red = Math.Clamp(r, 0, 255);
        green = Math.Clamp(g, 0, 255);
        blue = Math.Clamp(b, 0, 255);
        alpha = Math.Clamp(a, 0, 255);
        return (blue << 0) | (green << 8) | (red << 16) | (alpha << 24);
    }

    @Override
    public void Pixel(int x, int y, int col) {
        if (x < 0 || y < 0 || x >= curMode.XRes || y >= curMode.YRes) {
            return;
        }

        int addr32 = (x + y * curMode.XRes) << 2;
        int addrR32 = MAGIC.addr(buffer[addr32]);
        MAGIC.wMem32(addrR32, col);
        needsRedraw = true;
    }

    @Override
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

        if (x + width > curMode.XRes) {
            width = curMode.XRes - x;
        }

        if (y + height > curMode.YRes) {
            height = curMode.YRes - y;
        }

        // this should not happen but it does and im confused
        // somehow returning fixes it but it makes no sense
        if (width <= 0 || height <= 0) {
            return;
        }

        int addr32 = (x + y * curMode.XRes) << 2;
        int addrR32 = MAGIC.addr(buffer[addr32]);
        for (int i = 0; i < height; i++) {
            Memory.Memset32(addrR32, width, color);
            addrR32 += curMode.XRes << 2;
        }
    }

    @Override
    public void Bitmap(int x, int y, Bitmap bitmap) {
        if (curMode == null || bitmap == null) {
            Kernel.panic("VESAGraphics.setBitmap: mode or bitmap is null");
            return;
        }

        int height = Math.Clamp(bitmap.Height, 0, curMode.YRes - 1 - y);
        int width = Math.Clamp(bitmap.Width, 0, curMode.XRes - 1 - x);

        for (int pixel_y = 0; pixel_y < height; pixel_y++) {

            if (!bitmap.IsTransparent) {
                int startAddr = MAGIC.addr(bitmap.PixelData[bitmap.Row(pixel_y)]);
                int toAddr = MAGIC.addr(buffer[(x + (y + pixel_y) * curMode.XRes) << 2]);
                Memory.Memcopy32(startAddr, toAddr, width);
                continue;
            }

            for (int pixel_x = 0; pixel_x < width; pixel_x++) {
                int col = bitmap.GetPixel(pixel_x, pixel_y);
                int alpha = (col >> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }

                int xx = x + pixel_x;
                int yy = y + pixel_y;
                int addr32 = (xx + yy * curMode.XRes) << 2;
                int addrR32 = MAGIC.addr(buffer[addr32]);

                if (alpha == 255) {
                    MAGIC.wMem32(addrR32, col);
                } else {
                    int oldCol = MAGIC.rMem32(addrR32);
                    int newCol = Blend(col, oldCol);
                    MAGIC.wMem32(addrR32, newCol);
                }
            }
        }
        needsRedraw = true;
    }

    @SJC.Inline
    private int Blend(int withAlpha, int noAlpha) {
        int alpha = (withAlpha >> 24) & 0xFF;
        int r = (alpha * ((withAlpha >> 16) & 0xFF) + (255 - alpha) * ((noAlpha >> 16) & 0xFF)) / 255;
        int g = (alpha * ((withAlpha >> 8) & 0xFF) + (255 - alpha) * ((noAlpha >> 8) & 0xFF)) / 255;
        int b = (alpha * (withAlpha & 0xFF) + (255 - alpha) * (noAlpha & 0xFF)) / 255;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public void Swap() {
        if (needsRedraw) {
            int from = MAGIC.addr(buffer[0]);
            int to = curMode.LfbAddress;
            int len = buffer.length;
            Memory.Memcopy(from, to, len);
        }
        needsRedraw = false;
    }

    @Override
    public void ClearScreen() {
        int from = MAGIC.addr(buffer[0]);
        int len = buffer.length;
        Memory.Memset(from, len, (byte) 0);
    }

    @Override
    public boolean Contains(int x, int y) {
        return x >= 0 && y >= 0 && x < curMode.XRes && y < curMode.YRes;
    }
}
