package kernel.hardware.pci;

public class LazyPciDeviceReader extends PCI {
    private int currentBus;
    private int currentDevice;
    private int currentFunction;
    private boolean isFinished = false;

    public LazyPciDeviceReader() {
        currentBus = 0;
        currentDevice = 0;
        currentFunction = 0;
    }

    public PciDevice next() {
        PciDevice device = read(currentBus, currentDevice, currentFunction);
        currentFunction++;
        if (currentFunction >= MAX_FUNCTIONS) {
            currentFunction = 0;
            currentDevice++;
            if (currentDevice >= MAX_DEVICES) {
                currentDevice = 0;
                currentBus++;
                if (currentBus >= MAX_BUS) {
                    currentBus = MAX_BUS;
                    isFinished = true;
                }
            }
        }

        return device;
    }

    public PciDevice previous() {
        currentFunction--;
        if (currentFunction < 0) {
            currentFunction = MAX_FUNCTIONS - 1;
            currentDevice--;
            if (currentDevice < 0) {
                currentDevice = MAX_DEVICES - 1;
                currentBus--;
                if (currentBus < 0) {
                    currentBus = 0;
                    isFinished = true;
                }
            }
        }
        PciDevice device = read(currentBus, currentDevice, currentFunction);
        return device;
    }

    public boolean hasNext() {
        return !isFinished;
    }

    public void reset() {
        currentBus = 0;
        currentDevice = 0;
        currentFunction = 0;
        isFinished = false;
    }
}
