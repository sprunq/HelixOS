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

    @SJC.Inline
    public static void sleep(int ms) {
        double timerRate = PIT.getRateHz();
        int end = tickCount + (int) (ms * timerRate / 1000);
        while (tickCount < end) {
        }
    }
}
