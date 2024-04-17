package kernel.hardware;

public class Timer {
    private static int _tickCount = 0;

    @SJC.Inline
    public static void tick() {
        _tickCount++;
        if (_tickCount < 0) {
            _tickCount = 0;
        }
    }

    @SJC.Inline
    public static int getTick() {
        return _tickCount;
    }

    public static void sleep(int ms) {
        double rate = PIT.get_rateHz();
        int ticks = (int) (rate / 1000.0 * (double) ms);
        int start = getTick();
        while (getTick() - start < ticks) {
            // wait
        }
    }

    public static int getTickDifferenceMs(int start, int end) {
        double rate = PIT.get_rateHz();
        return (int) ((end - start) / rate * 1000.0);
    }

    public static int getTickDifferenceMs(int ticks) {
        return getTickDifferenceMs(0, ticks);
    }
}
