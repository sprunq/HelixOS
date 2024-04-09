package kernel.hardware;

public class Timer {
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

    public static void sleep(int ms) {
        double rate = PIT.getRateHz();
        int ticks = (int) (rate / 1000.0 * (double) ms);
        int start = getTick();
        while (getTick() - start < ticks) {
            // wait
        }
    }

    public static int getTickDifferenceMs(int start, int end) {
        double rate = PIT.getRateHz();
        return (int) ((end - start) / rate * 1000.0);
    }
}
