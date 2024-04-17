package kernel.display.vesa;

import kernel.Kernel;
import kernel.bios.BIOS;
import kernel.display.ADisplay;
import kernel.memory.Memory;
import util.BitHelper;
import util.MathH;

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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setPixel(x + j, y + i, color);
            }
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
            buffer[addr] = (byte) col;
            buffer[addr + 1] = (byte) (col >>> 8);
            buffer[addr + 2] = (byte) (col >>> 16);
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
}
