package kernel.interrupt;

public class PeriodicInterruptTimer {
    private static double rateHz = 18.2;

    public static void setRate(short hz) {
        int divisor = 1193180 / hz;
        MAGIC.wIOs8(0x43, (byte) 0x36);
        MAGIC.wIOs8(0x40, (byte) (divisor & 0xFF));
        MAGIC.wIOs8(0x40, (byte) (divisor >> 8));
        rateHz = hz;
    }

    @SJC.Inline
    public static double getRateHz() {
        return rateHz;
    }
}
