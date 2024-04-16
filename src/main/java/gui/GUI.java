package gui;

import kernel.Kernel;
import kernel.Logger;
import kernel.display.video.font.Font8x8;

public class GUI implements IUIElement {
    public Editor TfMain;
    public Homebar Homebar;
    public LogTextField Logs;
    public MemMapTextField MemMap;
    public PCIDeviceReader PciDeviceReader;
    public MultiWindow MultiWindow;

    private IUIElement[] _elements;

    public GUI() {
        int Width = Kernel.Display.Width();
        int Height = Kernel.Display.Height();

        double HHomebar = 20;

        Logger.info("GUI", "Creating Homebar");
        Homebar = new Homebar(
                0,
                (int) (Height - HHomebar),
                (int) Width,
                (int) HHomebar);

        double HEditor = Height - Homebar.Height;
        double WEditor = Width * 0.5;
        double XEditor = 0;
        double YEditor = 0;

        Logger.info("GUI", "Creating Editor");
        TfMain = new Editor(
                (int) XEditor,
                (int) YEditor,
                (int) WEditor,
                (int) HEditor,
                8,
                Font8x8.Instance,
                0,
                2,
                Kernel.Display.rgb(100, 100, 100),
                Kernel.Display.rgb(255, 255, 255));

        double HMultiWindow = Height - Homebar.Height;
        double WMultiWindow = Width * 0.5;

        double XMultiWindow = Width * 0.5;
        double YMultiWindow = 0;

        Logger.info("GUI", "Creating LogTextField");
        Logs = new LogTextField(
                (int) XMultiWindow,
                (int) YMultiWindow,
                (int) WMultiWindow,
                (int) HMultiWindow,
                8,
                0,
                2,
                Font8x8.Instance);

        Logger.info("GUI", "Creating MemMapTextField");
        MemMap = new MemMapTextField(
                (int) XMultiWindow,
                (int) YMultiWindow,
                (int) WMultiWindow,
                (int) HMultiWindow,
                2,
                Font8x8.Instance,
                0,
                2,
                Kernel.Display.rgb(0, 0, 0));

        PciDeviceReader = new PCIDeviceReader(
                (int) XMultiWindow,
                (int) YMultiWindow,
                (int) WMultiWindow,
                (int) HMultiWindow,
                2,
                Font8x8.Instance,
                0,
                2,
                Kernel.Display.rgb(0, 0, 0));

        MultiWindow = new MultiWindow(4);
        MultiWindow.addWindow(Logs);
        MultiWindow.addWindow(MemMap);
        MultiWindow.addWindow(PciDeviceReader);

        _elements = new IUIElement[3];
        _elements[1] = Homebar;
        _elements[0] = TfMain;
        _elements[2] = MultiWindow;

        drawBg();
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void drawFg() {
        for (int i = 0; i < _elements.length; i++) {
            IUIElement element = _elements[i];
            if (element.isDirty()) {
                element.drawFg();
            }
        }
    }

    @Override
    public void drawBg() {
        for (int i = 0; i < _elements.length; i++) {
            _elements[i].drawBg();
        }
    }
}
