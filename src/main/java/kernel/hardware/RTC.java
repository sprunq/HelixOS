package kernel.hardware;

public class RTC {
    public static int readRTCHour() {
        MAGIC.wIOs8(0x70, (byte) 4);
        int hours = MAGIC.rIOs8(0x71);
        hours = (hours & 0xF) + ((hours & 0x70) >> 4) * 10 + (hours & 0x80);
        return hours;
    }

    public static int readRTCMinute() {
        MAGIC.wIOs8(0x70, (byte) 2);
        int minutes = (int) MAGIC.rIOs8(0x71);
        minutes = (minutes & 0xF) + ((minutes >> 4) * 10);
        return minutes;
    }

    public static int readRTCSecond() {
        MAGIC.wIOs8(0x70, (byte) 0);
        int seconds = MAGIC.rIOs8(0x71);
        seconds = (seconds & 0xF) + ((seconds >> 4) * 10);
        return seconds;
    }
}
