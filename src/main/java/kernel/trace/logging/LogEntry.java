package kernel.trace.logging;

public class LogEntry {
    private String message;
    private String category;
    private byte priority;

    private String time_HMS;

    public LogEntry(String category, String message, byte priority, String time_HMS) {
        this.category = category;
        this.message = message;
        this.priority = priority;
        this.time_HMS = time_HMS;
    }

    @SJC.Inline
    public String Category() {
        return category;
    }

    @SJC.Inline
    public String Message() {
        return message;
    }

    @SJC.Inline
    public byte Priority() {
        return priority;
    }

    @SJC.Inline
    public String TimeHMS() {
        return time_HMS;
    }

    @SJC.Inline
    public void SetCategory(String category) {
        this.category = category;
    }

    @SJC.Inline
    public void SetMessage(String message) {
        this.message = message;
    }

    @SJC.Inline
    public void SetPriority(byte priority) {
        this.priority = priority;
    }

    @SJC.Inline
    public void SetTimeHMS(String time_HMS) {
        this.time_HMS = time_HMS;
    }
}
