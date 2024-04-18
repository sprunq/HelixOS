package kernel.display.vesa;

import kernel.Kernel;
import kernel.bios.call.DisplayModes;
import kernel.display.ADisplay;
import kernel.memory.Memory;
import util.MathH;
import util.logging.Logger;

public class VESAGraphics extends ADisplay {
    public VESAMode curMode;
    private byte[] buffer;
    private boolean needsRedraw;

    public VESAGraphics(VESAMode mode) {
        if (mode == null) {
            Kernel.panic("VESAGraphics.setMode: mode is null");
        }
        int size = mode.XRes * mode.YRes * mode.bytesPerColor();
        buffer = new byte[size];
        needsRedraw = true;
        curMode = mode;

        Logger.info("VESA", "setMode to ".append(curMode.dbg()));
    }

    @Override
    public void activate() {
        Logger.info("VESA", "activate mode ".append(curMode.dbg()));
        DisplayModes.setVesaMode(curMode.ModeNr);
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
    public int rgb(int r, int g, int b) {
        int red, green, blue;
        switch (curMode.ColorDepth) {
            case 8:
                return MathH.clamp(r, 0, 255);
            case 15:
            case 16:
                red = MathH.clamp(r, 0, 255) >> 3;
                green = MathH.clamp(g, 0, 255) >> 3;
                blue = MathH.clamp(b, 0, 255) >> 3;
                return (blue << 0) | (green << 5) | (red << 10);
            case 24:
                red = MathH.clamp(r, 0, 255);
                green = MathH.clamp(g, 0, 255);
                blue = MathH.clamp(b, 0, 255);
                return (blue << 0) | (green << 8) | (red << 16);
            case 32:
                red = MathH.clamp(r, 0, 255);
                green = MathH.clamp(g, 0, 255);
                blue = MathH.clamp(b, 0, 255);
                return (blue << 0) | (green << 8) | (red << 16) | (255 << 24);
            default:
                return 0;
        }
    }

    @Override
    public void setPixel(int x, int y, int col) {
        if (x < 0
                || y < 0
                || x >= curMode.XRes
                || y > curMode.YRes) {
            Kernel.panic("VESAGraphics.setPixel: invalid parameters(".append(x).append(", ").append(y).append(")"));
            return;
        }

        switch (curMode.ColorDepth) {
            case 8:
                int addr8 = x + y * curMode.XRes;
                int addrR8 = MAGIC.addr(buffer[addr8]);
                MAGIC.wMem8(addrR8, (byte) col);
                break;
            case 15:
            case 16:
                int addr16 = (x + y * curMode.XRes) << 1;
                int addrR16 = MAGIC.addr(buffer[addr16]);
                MAGIC.wMem16(addrR16, (short) col);
                break;
            case 24:
                int addr24 = (x + y * curMode.XRes) * 3;
                int addrR24 = MAGIC.addr(buffer[addr24]);
                MAGIC.wMem8(addrR24, (byte) col);
                MAGIC.wMem16(addrR24 + 1, (short) (col >> 8));
                break;
            case 32:
                int addr32 = (x + y * curMode.XRes) << 2;
                int addrR32 = MAGIC.addr(buffer[addr32]);
                MAGIC.wMem32(addrR32, col);
                break;
        }
        needsRedraw = true;
    }

    @Override
    public void fillrect(int x, int y, int width, int height, int color) {
        if (x < 0 || y < 0 || x + width > curMode.XRes || y + height > curMode.YRes) {
            Kernel.panic("VESAGraphics.fillrect: invalid parameters");
        }

        switch (curMode.ColorDepth) {
            case 8:
                fillrect8(x, y, width, height, color);
                break;
            case 15:
            case 16:
                fillrect16(x, y, width, height, color);
                break;
            case 24:
                fillrect24(x, y, width, height, color);
                break;
            case 32:
                fillrect32(x, y, width, height, color);
                break;
        }

    }

    @Override
    public void setBitmap(int x, int y, int[][] bitmap) {
        if (curMode == null
                || bitmap == null
                || bitmap.length == 0
                || bitmap[0].length == 0) {
            Kernel.panic("VESAGraphics.setBitmap: invalid parameters");
            return;
        }

        int height = bitmap.length;
        int width = bitmap[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int col = bitmap[i][j];
                setPixel(x + i, y + j, col);
            }
        }
    }

    @Override
    public void swap() {
        if (needsRedraw) {
            int from = MAGIC.addr(buffer[0]);
            int to = curMode.LfbAddress;
            int len = buffer.length;
            Memory.copyBytes(from, to, len);
        }
        needsRedraw = false;
    }

    @Override
    public void clearScreen() {
        int from = MAGIC.addr(buffer[0]);
        int len = buffer.length;
        Memory.setBytes(from, len, (byte) 0);
    }

    @SJC.Inline
    private void fillrect32(int x, int y, int width, int height, int color) {
        int addr32 = (x + y * curMode.XRes) << 2;
        int addrR32 = MAGIC.addr(buffer[addr32]);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                MAGIC.wMem32(addrR32 + (j << 2), color);
            }
            addrR32 += curMode.XRes << 2;
        }
    }

    @SJC.Inline
    private void fillrect24(int x, int y, int width, int height, int color) {
        int addr24 = (x + y * curMode.XRes) * 3;
        int addrR24 = MAGIC.addr(buffer[addr24]);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                MAGIC.wMem8(addrR24 + (j * 3), (byte) color);
                MAGIC.wMem16(addrR24 + (j * 3) + 1, (short) (color >> 8));
            }
            addrR24 += curMode.XRes * 3;
        }
    }

    @SJC.Inline
    private void fillrect16(int x, int y, int width, int height, int color) {
        int addr16 = (x + y * curMode.XRes) << 1;
        int addrR16 = MAGIC.addr(buffer[addr16]);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                MAGIC.wMem16(addrR16 + (j << 1), (short) color);
            }
            addrR16 += curMode.XRes << 1;
        }
    }

    @SJC.Inline
    private void fillrect8(int x, int y, int width, int height, int color) {
        int addr8 = x + y * curMode.XRes;
        int addrR8 = MAGIC.addr(buffer[addr8]);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                MAGIC.wMem8(addrR8 + j, (byte) color);
            }
            addrR8 += curMode.XRes;
        }
    }

}