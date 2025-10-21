package launcher.ui;

import launcher.model.Settings;
import launcher.model.ExecutableInfo;
import launcher.service.LocalizationService;
import launcher.service.PersistenceService;
import launcher.AppLogger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class MainFrameController {
    private final Settings settings;
    private final LocalizationService localization;
    private final List<ExecutableInfo> executables;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public MainFrameController(Settings settings, LocalizationService localization) {
        this.settings = settings;
        this.localization = localization;
        this.executables = new ArrayList<>();
    }

    public Settings getSettings() {
        return settings;
    }

    public LocalizationService getLocalization() {
        return localization;
    }

    public List<ExecutableInfo> getExecutables() {
        return executables;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addExecutable(ExecutableInfo info) {
        executables.add(info);
        pcs.firePropertyChange("executables", null, executables);
    }

    public void removeExecutable(ExecutableInfo info) {
        executables.remove(info);
        pcs.firePropertyChange("executables", null, executables);
    }

    public void saveSettings() {
        try {
            PersistenceService.saveSettings(settings);
        } catch (Exception ex) {
            AppLogger.saveSettingsError(ex.getMessage());
        }
    }

    public void saveExecutables() {
        try {
            PersistenceService.saveExecutables(executables);
        } catch (Exception ex) {
            AppLogger.saveBlocksError(ex.getMessage());
        }
    }
}
