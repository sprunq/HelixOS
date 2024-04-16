package kernel;

public class Logger {
    private static LogEntry[] logBuffer;
    private static int logIndex = 0;
    private static int entryCount = 0;
    private static byte minimumLogLevel = 0;
    private static boolean initialized = false;

    public static final byte NONE = 0;
    public static final byte TRACE = 1;
    public static final byte INFO = 2;
    public static final byte WARNING = 3;
    public static final byte ERROR = 4;
    public static final byte FATAL = 5;

    public static void initialize(byte logLevel, int capactiy) {
        logBuffer = new LogEntry[capactiy];
        for (int i = 0; i < capactiy; i++) {
            logBuffer[i] = new LogEntry("", "", NONE);
        }
        initialized = true;
        minimumLogLevel = logLevel;

        Logger.info("Logger", "Initialized");
    }

    @SJC.Inline
    public static void trace(String category, String message) {
        log(category, message, TRACE);
    }

    @SJC.Inline
    public static void info(String category, String message) {
        log(category, message, INFO);
    }

    @SJC.Inline
    public static void warning(String category, String message) {
        log(category, message, WARNING);
    }

    @SJC.Inline
    public static void error(String category, String message) {
        log(category, message, ERROR);
    }

    @SJC.Inline
    public static void fatal(String category, String message) {
        log(category, message, FATAL);
    }

    public static void log(String category, String message, byte priority) {
        if (priority < minimumLogLevel || !initialized)
            return;

        logBuffer[logIndex].setCategory(category);
        logBuffer[logIndex].setMessage(message);
        logBuffer[logIndex].setPriority(priority);
        logIndex++;
        if (logIndex >= logBuffer.length) {
            logIndex = 0;
        }
        if (entryCount < logBuffer.length) {
            entryCount++;
        }
    }

    @SJC.Inline
    public static LogEntry getChronologicalLog(int i) {
        return logBuffer[internalIndex(i)];
    }

    @SJC.Inline
    public static int getNumberOfLogs() {
        return entryCount;
    }

    @SJC.Inline
    private static int internalIndex(int i) {
        int index = (logIndex - i - 1);
        if (index < 0) {
            index += logBuffer.length;
        }
        return index;
    }
}
