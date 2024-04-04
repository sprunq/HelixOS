package kernel;

public class Logger {
    private static String[] logBuffer;
    private static int logIndex = 0;
    private static boolean initialized = false;

    public static void init(int capactiy) {
        logBuffer = new String[capactiy];
        for (int i = 0; i < logBuffer.length; i++) {
            logBuffer[i] = "";
        }
        initialized = true;
    }

    public static void log(String message) {
        if (!initialized) {
            return;
        }
        logBuffer[logIndex] = message;
        logIndex++;
        if (logIndex >= logBuffer.length) {
            logIndex = 0;
        }
    }

    public static String getChronologicalLog(int i) {
        return logBuffer[internalIndex(i)];
    }

    public static int getLogBufferSize() {
        return logBuffer.length;
    }

    private static int internalIndex(int i) {
        int index = logIndex - i - 1;
        if (index < 0) {
            index += logBuffer.length;
        }
        return index;
    }
}
