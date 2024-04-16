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

                ColorPalette = new ColorPalette(
                                100,
                                100,
                                300,
                                300,
                                8);

        }

        @Override
        public void draw() {
                ColorPalette.draw();
        }
}
