package kernel;

public class LogEntry {
    private String message;
    private byte priority;

    public LogEntry(String message, byte priority) {
        this.message = message;
        this.priority = priority;
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
    public void setMessage(String message) {
        this.message = message;
    }

    @SJC.Inline
    public void setPriority(byte priority) {
        this.priority = priority;
    }
}
