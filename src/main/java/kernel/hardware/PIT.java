package kernel.hardware;

public class PIT {
    private static final int PIT_A = 0x40;
    private static final int PIT_CTRL = 0x43;
    private static final byte PIT_SET = (byte) 0x36;
    private static double rateHz = 18.2;

    public static void setRate(int hz) {
        short divisor = (short) (1193131.666 / hz);
        MAGIC.wIOs8(PIT_CTRL, PIT_SET);
        MAGIC.wIOs8(PIT_A, (byte) (divisor & 0xFF));
        MAGIC.wIOs8(0x40, (byte) ((divisor >> 8) & 0xFF));
        rateHz = hz;
    }

    @SJC.Inline
    public static double getRateHz() {
        return rateHz;
    }
}
