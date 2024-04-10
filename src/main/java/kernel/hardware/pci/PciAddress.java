package kernel.hardware.pci;

public class PciAddress extends STRUCT {
    @SJC(offset = 2)
    byte register;

    @SJC(offset = 8)
    byte function;

    @SJC(offset = 11)
    byte deviceNumber;

    @SJC(offset = 16)
    byte busNumber;

    @SJC(offset = 24)
    byte edcReserved;
}
