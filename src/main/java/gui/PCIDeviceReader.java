package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.AFont;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import kernel.hardware.pci.LazyPciDeviceReader;
import kernel.hardware.pci.PciDevice;
import util.StrBuilder;

public class PCIDeviceReader extends TextField implements IKeyboardEventListener {
    private LazyPciDeviceReader pciDeviceReader;
    private PciDevice device;
    private StrBuilder sb;

    public PCIDeviceReader(
            int x,
            int y,
            int width,
            int height,
            int border,
            AFont font,
            int charSpacing,
            int lineSpacing,
            byte backGroundColor) {
        super(x, y, width, height, border, charSpacing, lineSpacing, backGroundColor, VM13.frgb(1.0, 1.0, 1.0), font);
        this.pciDeviceReader = new LazyPciDeviceReader();
        this.device = pciDeviceReader.next();
        this.sb = new StrBuilder(400);
    }

    public void draw() {
        clearText();
        sb.clearKeepSize();
        sb.appendLine("PCI Devices")
                .appendLine()
                .appendLine("- right arrow -> next")
                .appendLine();

        if (device != null) {
            sb.appendLine(device.baseClassName())
                    .append("Bus: ").appendLine(device.Bus)
                    .append("Device: ").appendLine(device.Device)
                    .append("Function: ").appendLine(device.Function)
                    .append("Vendor ID: ").appendLine(device.VendorId)
                    .append("Device ID: ").appendLine(device.DeviceId)
                    .append("Command: ").appendLine(device.Command)
                    .append("Status: ").appendLine(device.Status)
                    .append("Revision: ").appendLine(device.Revision)
                    .append("Interface: ").appendLine(device.Itf)
                    .append("Subclass Code: ").appendLine(device.SubClassCode)
                    .append("Base Class Code: ").appendLine(device.BaseClassCode)
                    .append("Class: ").appendLine(device.Cls)
                    .append("Latency: ").appendLine(device.Latency)
                    .append("Header: ").appendLine(device.Header)
                    .append("Bist: ").appendLine(device.Bist);
        } else {
            sb.appendLine("null");
        }

        addString(sb.toString());
        super.draw();
    }

    @Override
    public boolean onKeyPressed(char keyCode) {
        switch ((int) keyCode) {
            case Key.ARROW_RIGHT:
                do {
                    if (!pciDeviceReader.hasNext()) {
                        pciDeviceReader.reset();
                    }
                    device = pciDeviceReader.next();
                } while (device == null);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        return false;
    }
}
