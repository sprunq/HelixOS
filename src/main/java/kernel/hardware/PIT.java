package kernel.hardware;

import kernel.Logger;

/**
 * The Programmable Interval Timer class represents a hardware timer used
 * for generating periodic interrupts.
 */
public class PIT {
    private static final int PIT_A = 0x40;
    private static final int PIT_CTRL = 0x43;
    private static final byte PIT_SET = (byte) 0x36;
    private static final double INTERNAL_CLOCK_SPEED = 1193131.666;
    private static double rateHz = 18.2;

    public static void init() {
        setRate(100);
        Logger.info("Set PIT rate to 100Hz");
    }

    /**
     * Sets the rate of the PIT timer.
     * 
     *
     * @param hz The desired rate in Hz.
     */
    public static void setRate(int hz) {
        short divisor = (short) (INTERNAL_CLOCK_SPEED / hz);
        MAGIC.wIOs8(PIT_CTRL, PIT_SET);
        MAGIC.wIOs8(PIT_A, (byte) (divisor & 0xFF));
        MAGIC.wIOs8(0x40, (byte) ((divisor >> 8) & 0xFF));
        rateHz = hz;
    }

    /**
     * Returns the current rate of the PIT timer in Hz.
     *
     * @return The current rate in Hz.
     */
    @SJC.Inline
    public static double getRateHz() {
        return rateHz;
    }
}
