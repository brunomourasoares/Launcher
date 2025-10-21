package launcher;

import launcher.service.LocalizationService;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AppLogger {
    private static JTextArea logArea;
    private static LocalizationService localization;
    private static final List<LogEvent> events = new ArrayList<>();

    private static class LogEvent {
        String key;
        Object[] params;
        boolean isText;
        LogEvent(String key, boolean isText, Object... params) {
            this.key = key;
            this.params = params;
            this.isText = isText;
        }
    }

    public static synchronized void init(JTextArea area, LocalizationService loc) {
        logArea = area;
        localization = loc;
        refreshLog();
    }

    public static synchronized void updateLocalization(LocalizationService loc) {
        localization = loc;
        refreshLog();
    }

    public static synchronized void clear() {
        events.clear();
        if (logArea != null) {
            logArea.setText("");
        }
    }

    private static synchronized void refreshLog() {
        if (logArea == null || localization == null) return;
        logArea.setText("");
        for (LogEvent event : events) {
            if (event.isText) {
                logArea.append(localization.getString(event.key) + "\n");
            } else {
                logArea.append(localization.getFormatted(event.key, event.params) + "\n");
            }
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static synchronized void info(String key, Object... params) {
        events.add(new LogEvent(key, false, params));
        refreshLog();
    }

    public static synchronized void warning(String key, Object... params) {
        events.add(new LogEvent(key, false, params));
        refreshLog();
    }

    public static synchronized void error(String key, Object... params) {
        events.add(new LogEvent(key, false, params));
        refreshLog();
    }

    public static synchronized void text(String key) {
        events.add(new LogEvent(key, true));
        refreshLog();
    }

    public static void created(String path) { info("log.block.created", path); }
    public static void waitDelay(String path) { info("log.block.waitDelay", path); }
    public static void opened(String path) { info("log.block.executableOpened", path); }
    public static void closed(String path) { info("log.block.executableClosed", path); }
    public static void monitoring(String name) { info("log.block.processMonitoring", name); }
    public static void noConfig() { warning("log.config.noConfig"); }
    public static void saveSettingsError(String msg) { error("log.block.saveSettingsError", msg); }
    public static void saveBlocksError(String msg) { error("log.block.saveBlocksError", msg); }
    public static void clearBlocksError(String msg) { error("log.block.clearBlocksError", msg); }
    public static void runError(String msg) { error("log.block.runError", msg); }
    public static void noSavedBlocks() { warning("log.noSavedBlocks"); }
    public static void removing(String path) { info("log.block.removing", path); }
}