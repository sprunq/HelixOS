package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font3x6;
import kernel.display.video.font.Font5x7;

public class GUI implements IUiElement {
    public TextField tfMain;
    public Homebar homebar;
    public ColorPalette colorPalette;
    public TextField tfLogs;
    public TextField tfMemMap;
    public MultiWindow multiWindow;

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
                200,
                200 - homebar.height,
                2,
                0, 2,
                VM13.frgb(0.7, 0.7, 0.7),
                VM13.frgb(1.0, 1.0, 1.0),
                Font5x7.Instance);

        int tfMainEndX = tfMain.x + tfMain.width + border;

        int multiWindowX = tfMainEndX;
        int multiWindowY = tfMain.y;
        int multiWindowWidth = 320 - tfMainEndX - border;
        int multiWindowHeight = 200 - homebar.height - border;

        tfLogs = new LogTextField(
                multiWindowX,
                multiWindowY,
                multiWindowWidth,
                multiWindowHeight,
                2,
                Font3x6.Instance,
                0,
                2,
                VM13.frgb(0.4, 0.4, 0.4));

        tfMemMap = new MemMapTextField(
                multiWindowX,
                multiWindowY,
                multiWindowWidth,
                multiWindowHeight,
                2,
                Font3x6.Instance,
                0,
                2,
                VM13.frgb(0.4, 0.4, 0.4));

        colorPalette = new ColorPalette(
                multiWindowX,
                multiWindowY,
                multiWindowWidth,
                multiWindowHeight,
                8);

        multiWindow = new MultiWindow(3);
        multiWindow.addWindow(tfLogs);
        multiWindow.addWindow(tfMemMap);
        multiWindow.addWindow(colorPalette);
    }

    @Override
    public void draw() {
        tfMain.draw();
        multiWindow.draw();
        homebar.draw();
    }

    @Override
    public void clearDrawing() {
        homebar.clearDrawing();
        tfMain.clearDrawing();
        multiWindow.clearDrawing();
    }
}
