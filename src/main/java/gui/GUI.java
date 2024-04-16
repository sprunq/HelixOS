package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.Font3x6;

public class GUI implements IUiElement {
        public Editor TfMain;
        public Homebar Homebar;
        public ColorPalette ColorPalette;
        public LogTextField Logs;
        public MemMapTextField MemMap;
        public PCIDeviceReader PciDeviceReader;
        public MultiWindow MultiWindow;

        public GUI() {
                int border = 4;
                Homebar = new Homebar(
                                0,
                                200 - 16,
                                320,
                                16);

                TfMain = new Editor(
                                border,
                                border,
                                170,
                                200 - Homebar.Height,
                                2,
                                Font3x6.Instance,
                                0,
                                2,
                                VM13.frgb(0.7, 0.7, 0.7),
                                VM13.frgb(1.0, 1.0, 1.0));

                int tfMainEndX = TfMain.X + TfMain.Width + border;

                int multiWindowX = tfMainEndX;
                int multiWindowY = TfMain.Y;
                int multiWindowWidth = 320 - tfMainEndX - border;
                int multiWindowHeight = 200 - Homebar.Height - border;

                Logs = new LogTextField(
                                multiWindowX,
                                multiWindowY,
                                multiWindowWidth,
                                multiWindowHeight,
                                2,
                                Font3x6.Instance,
                                0,
                                2,
                                VM13.frgb(0.4, 0.4, 0.4));

                MemMap = new MemMapTextField(
                                multiWindowX,
                                multiWindowY,
                                multiWindowWidth,
                                multiWindowHeight,
                                2,
                                Font3x6.Instance,
                                0,
                                2,
                                VM13.frgb(0.4, 0.4, 0.4));

                PciDeviceReader = new PCIDeviceReader(
                                multiWindowX,
                                multiWindowY,
                                multiWindowWidth,
                                multiWindowHeight,
                                2,
                                Font3x6.Instance,
                                0,
                                2,
                                VM13.frgb(0.4, 0.4, 0.4));

                ColorPalette = new ColorPalette(
                                multiWindowX,
                                multiWindowY,
                                multiWindowWidth,
                                multiWindowHeight,
                                8);

                MultiWindow = new MultiWindow(4);
                MultiWindow.addWindow(Logs);
                MultiWindow.addWindow(MemMap);
                MultiWindow.addWindow(PciDeviceReader);
                MultiWindow.addWindow(ColorPalette);
        }

        @Override
        public void draw() {
                TfMain.draw();
                MultiWindow.draw();
                Homebar.draw();
        }
}
