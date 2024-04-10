package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.AFont;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import kernel.hardware.pci.LazyPciDeviceReader;
import kernel.hardware.pci.PciDevice;

public class PCIDeviceReader extends TextField implements IKeyboardEventListener {
    private LazyPciDeviceReader pciDeviceReader;
    private PciDevice device;

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
    }

    public void draw() {
        clearText();

        addString("PCI Devices");
        newLine();
        newLine();
        addString("- left arrow -> previous");
        newLine();
        addString("- right arrow -> next");
        newLine();
        newLine();

        if (device != null) {
            String bus = Integer.toString(device.Bus, 10);
            String dev = Integer.toString(device.Device, 10);
            String function = Integer.toString(device.Function, 10);

            String vendorId = Integer.toString(device.VendorId, 10);
            String deviceId = Integer.toString(device.DeviceId, 10);
            String command = Integer.toString(device.Command, 10);
            String status = Integer.toString(device.Status, 10);
            String revision = Integer.toString(device.Revision, 10);
            String itf = Integer.toString(device.Itf, 10);
            String subClassCode = Integer.toString(device.SubClassCode, 10);
            String baseClassCode = Integer.toString(device.BaseClassCode, 10);
            String cls = Integer.toString(device.Cls, 10);
            String latency = Integer.toString(device.Latency, 10);
            String header = Integer.toString(device.Header, 10);
            String bist = Integer.toString(device.Bist, 10);

            String baseClassName = device.baseClassName();

            addStringln(baseClassName);
            addString("Bus: ");
            addStringln(bus);
            addString("Device: ");
            addStringln(dev);
            addString("Function: ");
            addStringln(function);
            addString("Vendor ID: ");
            addStringln(vendorId);
            addString("Device ID: ");
            addStringln(deviceId);
            addString("Command: ");
            addStringln(command);
            addString("Status: ");
            addStringln(status);
            addString("Revision: ");
            addStringln(revision);
            addString("Interface: ");
            addStringln(itf);
            addString("Subclass Code: ");
            addStringln(subClassCode);
            addString("Base Class Code: ");
            addStringln(baseClassCode);
            addString("Class: ");
            addStringln(cls);
            addString("Latency: ");
            addStringln(latency);
            addString("Header: ");
            addStringln(header);
            addString("Bist: ");
            addStringln(bist);

        } else {
            addString("null");
        }

        super.draw();
    }

    @Override
    public boolean onKeyPressed(char keyCode) {
        int loops = 0;
        switch ((int) keyCode) {
            case Key.ARROW_LEFT:
                do {
                    if (!pciDeviceReader.hasNext()) {
                        pciDeviceReader.reset();
                    }
                    device = pciDeviceReader.previous();
                    loops++;
                } while (device == null && loops < 1000000);
                return true;
            case Key.ARROW_RIGHT:
                do {
                    if (!pciDeviceReader.hasNext()) {
                        pciDeviceReader.reset();
                    }
                    device = pciDeviceReader.next();
                    loops++;
                } while (device == null && loops < 1000000);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        return false;
    }
}
