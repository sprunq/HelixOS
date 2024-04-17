package kernel.display.vesa;

import kernel.Kernel;
import kernel.bios.BIOS;
import kernel.display.ADisplay;
import kernel.memory.Memory;
import util.BitHelper;
import util.MathH;
import util.logging.Logger;

public class VESAGraphics extends ADisplay {
    public VESAMode curMode;
    private byte[] buffer;
    private boolean needsRedraw;

    public VESAGraphics() {
        curMode = null;
    }

    public void setMode(VESAMode mode) {
        if (mode == null) {
            Kernel.panic("VESAGraphics.setMode: mode is null");
        }
        BIOS.Registers.EAX = 0x4F02; // set current mode
        BIOS.Registers.EBX = mode.ModeNr;
        BIOS.rint(0x10);
        curMode = mode;
        buffer = new byte[curMode.XRes * curMode.YRes * 3];
        needsRedraw = true;
        Logger.info("VESA", "setMode to ".append(curMode.dbg()));
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
    public void fillrect(int x, int y, int width, int height, int color) {
        if (curMode == null) {
            Kernel.panic("VESAGraphics.fillrect: mode is null");
        }
        if (x < 0 || y < 0 || x + width > curMode.XRes || y + height > curMode.YRes) {
            Kernel.panic("VESAGraphics.fillrect: invalid parameters");
        }

        if (curMode.ColorDepth == 24) {
            fillrect24(x, y, width, height, color);
        } else {
            Kernel.panic("VESAGraphics.fillrect: unsupported color depth");
        }

    }

    @Override
    public void setBitmap(int x, int y, int[][] bitmap) {
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
    public void setPixel(int x, int y, int col) {
        if (curMode == null
                || x < 0
                || y < 0
                || x >= curMode.XRes
                || y > curMode.YRes) {
            Kernel.panic("VESAGraphics.setPixel: invalid parameters");
            return;
        }

        if (curMode.ColorDepth == 24) {
            int addr = (x + y * curMode.XRes) * 3;
            int addrR = MAGIC.addr(buffer[addr]);
            MAGIC.wMem8(addrR, (byte) col);
            MAGIC.wMem16(addrR + 1, (short) (col >> 8));
        } else {
            Kernel.panic("VESAGraphics.setPixel: unsupported color depth");
        }
        needsRedraw = true;
    }

    @Override
    public int rgb(int r, int g, int b) {
        int red = MathH.clamp(r, 0, 255);
        int green = MathH.clamp(g, 0, 255);
        int blue = MathH.clamp(b, 0, 255);
        int color = 0;
        color = BitHelper.setRange(color, 0, 8, blue);
        color = BitHelper.setRange(color, 8, 8, green);
        color = BitHelper.setRange(color, 16, 8, red);
        return color;
    }

    @Override
    public void swap() {
        if (needsRedraw) {
            if (curMode.ColorDepth == 24) {
                int from = MAGIC.addr(buffer[0]);
                int to = curMode.LfbAddress;
                int len = buffer.length;
                Memory.copyBytes(from, to, len);
            } else {
                Kernel.panic("VESAGraphics.update: unsupported color depth");
            }
        }
        needsRedraw = false;
    }

    @Override
    public void clearScreen() {
        int from = MAGIC.addr(buffer[0]);
        int len = buffer.length;
        Memory.setBytes(from, len, (byte) 0);
    }

    /*
     * Fill a rectangle with a color in 24-bit mode.
     * Somwhat optimized version.
     */
    @SJC.Inline
    private void fillrect24(int x, int y, int width, int height, int color) {
        int lineStart = (x + y * curMode.XRes) * 3;
        int lastLine = (x + (y + height) * curMode.XRes) * 3;
        for (int i = lineStart; i < lastLine; i += curMode.XRes * 3) {
            int lineStartAddr = MAGIC.addr(buffer[i]);
            for (int j = 0; j < width * 3; j += 3) {
                MAGIC.wMem8(lineStartAddr + j, (byte) color);
                MAGIC.wMem16(lineStartAddr + j + 1, (short) (color >> 8));
            }
        }
    }
}
