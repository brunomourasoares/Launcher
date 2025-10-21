package launcher.model;

import java.io.Serializable;

public class ExecutableInfo implements Serializable {
    private String path;
    private int delay;
    private Status status;
    private boolean enabled;
    private String processName;

    public enum Status {
        NOT_EXECUTED, EXECUTING, EXECUTED, CLOSED
    }

    public ExecutableInfo(String path, int delay, boolean enabled) {
        this.path = path;
        this.delay = delay;
        this.enabled = enabled;
        this.status = Status.NOT_EXECUTED;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public int getDelay() { return delay; }
    public void setDelay(int delay) { this.delay = delay; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String[] getProcessNames() {
        String names = processName == null ? extractExeName(path) : processName;
        return names.split("\\s*,\\s*");
    }
    public void setProcessName(String processName) { this.processName = processName; }
    private String extractExeName(String path) {
        String name = path.replace("\\", "/");
        int idx = name.lastIndexOf('/');
        if (idx >= 0) name = name.substring(idx + 1);
        return name;
    }
}
