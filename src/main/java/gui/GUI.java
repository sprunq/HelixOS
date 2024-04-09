package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font3x6;
import kernel.display.video.font.Font5x7;

public class GUI implements IUiElement {
    public TextField tfMain;
    public Homebar homebar;
    public ColorPalette colorPalette;
    public TextField tfLogs;

    public GUI() {
        int border = 4;
        homebar = new Homebar(
                0,
                200 - 16,
                320,
                16);

        tfMain = new TextField(
                border,
                border,
                40,
                200 - homebar.height,
                2,
                0, 2,
                VM13.frgb(0.7, 0.7, 0.7),
                VM13.frgb(1.0, 1.0, 1.0),
                Font5x7.Instance);

        int tfMainEndX = tfMain.x + tfMain.width + border;
        /*
         * 
         * colorPalette = new ColorPalette(
         * tfMainEndX,
         * tfMain.y,
         * 320 - tfMainEndX - border,
         * 20,
         * 3);
         */

        tfLogs = new LogTextField(
                tfMainEndX,
                tfMain.y,
                320 - tfMainEndX - border,
                tfMain.height - border,
                2,
                Font3x6.Instance,
                0,
                2,
                VM13.frgb(0.4, 0.4, 0.4));
    }

    @Override
    public void draw() {
        tfMain.draw();
        colorPalette.draw();
        tfLogs.draw();
        homebar.draw();
    }

    @Override
    public void clearDrawing() {
        homebar.clearDrawing();
        tfMain.clearDrawing();
        colorPalette.clearDrawing();
        tfLogs.clearDrawing();
    }
}
