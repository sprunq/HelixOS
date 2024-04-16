package gui;

import kernel.display.video.VM13;
import kernel.display.video.font.AFont;
import kernel.hardware.keyboard.IKeyboardEventListener;
import kernel.hardware.keyboard.Key;
import kernel.hardware.pci.LazyPciDeviceReader;
import kernel.hardware.pci.PciDevice;
import util.StrBuilder;

public class PCIDeviceReader extends TextField implements IKeyboardEventListener {
    private LazyPciDeviceReader _pciDeviceReader;
    private PciDevice _selectedDevice;
    private StrBuilder _sb;

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
        this._pciDeviceReader = new LazyPciDeviceReader();
        this._selectedDevice = _pciDeviceReader.next();
        this._sb = new StrBuilder(400);
    }

    public void draw() {
        clearText();
        _sb.clearKeepCapacity();
        _sb.appendLine("PCI Devices")
                .appendLine()
                .appendLine("- right arrow -> next")
                .appendLine();

        if (_selectedDevice != null) {
            _sb.appendLine(_selectedDevice.baseClassName())
                    .append("Bus: ").appendLine(_selectedDevice.Bus)
                    .append("Device: ").appendLine(_selectedDevice.Device)
                    .append("Function: ").appendLine(_selectedDevice.Function)
                    .append("Vendor ID: ").appendLine(_selectedDevice.VendorId)
                    .append("Device ID: ").appendLine(_selectedDevice.DeviceId)
                    .append("Command: ").appendLine(_selectedDevice.Command)
                    .append("Status: ").appendLine(_selectedDevice.Status)
                    .append("Revision: ").appendLine(_selectedDevice.Revision)
                    .append("Interface: ").appendLine(_selectedDevice.Itf)
                    .append("Subclass Code: ").appendLine(_selectedDevice.SubClassCode)
                    .append("Base Class Code: ").appendLine(_selectedDevice.BaseClassCode)
                    .append("Class: ").appendLine(_selectedDevice.Cls)
                    .append("Latency: ").appendLine(_selectedDevice.Latency)
                    .append("Header: ").appendLine(_selectedDevice.Header)
                    .append("Bist: ").appendLine(_selectedDevice.Bist);
        } else {
            _sb.appendLine("null");
        }

        addString(_sb.toString());
        super.draw();
    }

    @Override
    public boolean onKeyPressed(char keyCode) {
        switch ((int) keyCode) {
            case Key.ARROW_RIGHT:
                do {
                    if (!_pciDeviceReader.hasNext()) {
                        _pciDeviceReader.reset();
                    }
                    _selectedDevice = _pciDeviceReader.next();
                } while (_selectedDevice == null);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(char keyCode) {
        return false;
    }
}
