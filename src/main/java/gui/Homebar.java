package gui;

import kernel.display.videomode.Ascii8x8;
import kernel.display.videomode.IFont;
import kernel.display.videomode.VidWriter;
import kernel.lib.SystemClock;

public class Homebar implements IUiElement {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public IFont font;
    public TextField clock;

    public Homebar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = Ascii8x8.getInstance();
        this.clock = new TextField(320 - 63, 200 - 11, 80, 9, this.font, 1, 1, (byte) 200);
    }

    @Override
    public void draw() {
        VidWriter.setRegion(x, y, width, height, (byte) 100);

        // draw uptime
        int uptime = (int) SystemClock.asSeconds();
        String uptimeStr = Integer.toString(uptime, 10);
        String padded = uptimeStr.leftPad(6, ' ');
        clock.clear();
        clock.addString(padded);
        clock.addString("s");
        clock.draw();
    }

    @Override
    public void clearRegion() {
        clock.clearRegion();
        VidWriter.setRegion(x, y, width, height, (byte) 0);
    }

}
