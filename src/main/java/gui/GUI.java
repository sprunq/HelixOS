package gui;

import kernel.display.vesa.VESAGraphics;
import kernel.display.video.VM13;
import kernel.display.video.font.Font3x6;
import kernel.display.video.font.Font8x8;

public class GUI implements IUiElement {
        public Editor TfMain;
        public Homebar Homebar;
        public ColorPalette ColorPalette;
        public LogTextField Logs;
        public MemMapTextField MemMap;
        public PCIDeviceReader PciDeviceReader;
        public MultiWindow MultiWindow;

        public GUI() {

                Logs = new LogTextField(
                                0,
                                0,
                                800,
                                600,
                                2,
                                Font8x8.Instance,
                                0,
                                2,
                                VESAGraphics.rgb24(120, 120, 20));

        }

        @Override
        public void draw() {
                Logs.draw();
        }
}
