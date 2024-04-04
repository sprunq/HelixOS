package gui;

import kernel.display.video.font.Ascii8x8;

/*
 * The OS Desktop.
 */
public class GUI implements IUiElement {
    public TextField tfMain;
    public Homebar homebar;
    public ColorPalette colorPalette;
    public TextField tfLogs;

    public GUI() {
        byte backgroundColor = (byte) 245;
        byte textColor = (byte) 15;
        homebar = new Homebar(0, 200 - 16, 320, 16);

        tfMain = new TextField(
                5, 5,
                150, 200 - homebar.height - 1 - 8,
                Ascii8x8.getInstance(),
                0, 1,
                textColor, backgroundColor);

        int tfMainEndX = tfMain.x + tfMain.width;
        colorPalette = new ColorPalette(
                tfMainEndX + 5, tfMain.y,
                320 - tfMainEndX - 10, 30, 4);

        int cpEndY = colorPalette.y + colorPalette.height;
        tfLogs = new LogTextField(
                colorPalette.x, cpEndY + 5,
                colorPalette.width, tfMain.height - colorPalette.height - 5,
                Ascii8x8.getInstance(), 0, 1,
                textColor, backgroundColor);
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
