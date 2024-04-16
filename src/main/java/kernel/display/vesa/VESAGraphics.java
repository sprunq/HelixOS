package kernel.display.vesa;

import kernel.Kernel;
import kernel.bios.BIOS;
import kernel.display.ADisplay;
import util.BitHelper;
import util.MathH;

public class VesaGraphics extends ADisplay {
    public VesaMode curMode;

    public VesaGraphics() {
        curMode = null;
    }

    public void setMode(VesaMode mode) {
        if (mode == null) {
            Kernel.panic("VESAGraphics.setMode: mode is null");
        }
        BIOS.Registers.EAX = 0x4F02; // set current mode
        BIOS.Registers.EBX = mode.ModeNr;
        BIOS.rint(0x10);
        curMode = mode;
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
        for (int i = 0; i < bitmap.length; i++) {
            for (int j = 0; j < bitmap[i].length; j++) {
                setPixel(x + i, y + j, bitmap[i][j]);
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
            int addr = curMode.LfbAddress + (x + y * curMode.XRes) * 3;
            MAGIC.wMem16(addr, (short) col);
            MAGIC.wMem8(addr + 2, (byte) (col >>> 16));
        } else {
            Kernel.panic("VESAGraphics.setPixel: unsupported color depth");
        }
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

}
