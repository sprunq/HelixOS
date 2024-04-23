package kernel.trace.logging;

import kernel.hardware.RTC;
import util.StrBuilder;
import util.queue.QueueLogEntry;

public class Logger {
    private static QueueLogEntry logBuffer;
    private static byte minimumLogLevel = 0;
    private static boolean initialized = false;
    private static int logTicks = 0;

    public static final byte NONE = 0;
    public static final byte TRACE = 1;
    public static final byte INFO = 2;
    public static final byte WARNING = 3;
    public static final byte ERROR = 4;
    public static final byte FATAL = 5;

    public static void initialize(byte logLevel, int capactiy) {
        logBuffer = new QueueLogEntry(capactiy);
        for (int i = 0; i < capactiy; i++) {
            logBuffer.put(new LogEntry("", "", NONE, ""));
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

        LogEntry log = logBuffer.get();
        log.setCategory(category);
        log.setMessage(message);
        log.setPriority(priority);
        log.setTime_HMS(getTimeHMS());
        logBuffer.put(log);
        logTicks++;
    }

    @SJC.Inline
    public static LogEntry getChronologicalLog(int i) {
        return logBuffer.peek_back(i);
    }

    @SJC.Inline
    public static int getLogTicks() {
        return logTicks;
    }

    private static String getTimeHMS() {
        int hours = RTC.readHour();
        int minutes = RTC.readMinute();
        int seconds = RTC.readSecond();
        boolean hoursIsTwoDigits = hours >= 10;
        boolean minutesIsTwoDigits = minutes >= 10;
        boolean secondsIsTwoDigits = seconds >= 10;
        StrBuilder sb = new StrBuilder(10);
        if (!hoursIsTwoDigits) {
            sb.append('0');
        }
        sb.append(hours);
        sb.append(':');
        if (!minutesIsTwoDigits) {
            sb.append('0');
        }
        sb.append(minutes);
        sb.append(':');
        if (!secondsIsTwoDigits) {
            sb.append('0');
        }
        sb.append(seconds);
        return sb.toString();
    }
}
