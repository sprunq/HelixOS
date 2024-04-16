package kernel;

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
    public String getCategory() {
        return category;
    }

    @SJC.Inline
    public String getMessage() {
        return message;
    }

    @SJC.Inline
    public byte getPriority() {
        return priority;
    }

    @SJC.Inline
    public String getTime_HMS() {
        return time_HMS;
    }

    @SJC.Inline
    public void setCategory(String category) {
        this.category = category;
    }

    @SJC.Inline
    public void setMessage(String message) {
        this.message = message;
    }

    @SJC.Inline
    public void setPriority(byte priority) {
        this.priority = priority;
    }

    @SJC.Inline
    public void setTime_HMS(String time_HMS) {
        this.time_HMS = time_HMS;
    }
}
