package kernel.lib;

import kernel.interrupt.PeriodicInterruptTimer;

public class SystemClock {
    private static int tickCount = 0;

    @SJC.Inline
    public static void tick() {
        tickCount++;
        if (tickCount < 0) {
            tickCount = 0;
        }
    }

    @SJC.Inline
    public static int getTick() {
        return tickCount;
    }

    @SJC.Inline
    public static void sleep(int ms) {
        double timerRate = PeriodicInterruptTimer.getRateHz();
        int end = tickCount + (int) (ms * timerRate / 1000);
        while (tickCount < end) {
        }
    }

    @SJC.Inline
    public static double asSeconds() {
        return (double) tickCount / PeriodicInterruptTimer.getRateHz();
    }

    @SJC.Inline
    public static int tickToMilliseconds(int ticks) {
        return (int) (ticks * 1000 / PeriodicInterruptTimer.getRateHz());
    }
}
