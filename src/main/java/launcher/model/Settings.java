package launcher.model;

import java.io.Serializable;

public class Settings implements Serializable {
    private String language;
    private boolean autoStart;
    private boolean saveData;
    private int timeout;
    private boolean restartAllOnClose;
    private String theme;

    public Settings(String language, boolean autoStart, boolean saveData) {
        this.language = language;
        this.autoStart = autoStart;
        this.saveData = saveData;
        this.timeout = 30;
        this.restartAllOnClose = false;
        this.theme = "light";
    }

    public Settings(String language, boolean autoStart, boolean saveData, int defaultTimeout) {
        this.language = language;
        this.autoStart = autoStart;
        this.saveData = saveData;
        this.timeout = defaultTimeout;
        this.restartAllOnClose = false;
        this.theme = "light";
    }

    // Getters e setters
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    public boolean isSaveData() { return saveData; }
    public void setSaveData(boolean saveData) { this.saveData = saveData; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public boolean isRestartAllOnClose() { return restartAllOnClose; }
    public void setRestartAllOnClose(boolean restartAllOnClose) { this.restartAllOnClose = restartAllOnClose; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
