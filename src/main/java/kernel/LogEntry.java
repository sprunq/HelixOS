package kernel;

public class LogEntry {
    private String message;
    private byte priority;

    public LogEntry(String message, byte priority) {
        this.message = message;
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public byte getPriority() {
        return priority;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }
}
