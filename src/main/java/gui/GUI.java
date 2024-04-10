package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font3x6;

public class GUI implements IUiElement {
    public Editor tfMain;
    public Homebar homebar;
    public ColorPalette colorPalette;
    public LogTextField tfLogs;
    public MemMapTextField tfMemMap;
    public PCIDeviceReader pciDeviceReader;
    public MultiWindow multiWindow;

    public GUI() {
        int border = 4;
        homebar = new Homebar(
                0,
                200 - 16,
                320,
                16);

        tfMain = new Editor(
                border,
                border,
                170,
                200 - homebar.height,
                2,
                Font3x6.Instance,
                0,
                2,
                VM13.frgb(0.7, 0.7, 0.7),
                VM13.frgb(1.0, 1.0, 1.0));

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

        pciDeviceReader = new PCIDeviceReader(
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

        multiWindow = new MultiWindow(4);
        multiWindow.addWindow(tfLogs);
        multiWindow.addWindow(tfMemMap);
        multiWindow.addWindow(pciDeviceReader);
        multiWindow.addWindow(colorPalette);
    }

    @Override
    public void draw() {
        tfMain.draw();
        multiWindow.draw();
        homebar.draw();
    }
}
