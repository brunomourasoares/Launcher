package launcher;

import launcher.model.Settings;
import launcher.service.LocalizationService;

public class Injector {
    private static LocalizationService localizationService;
    private static Settings settings;

    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings("pt", false, false, 60);
        }
        return settings;
    }

    public static LocalizationService getLocalizationService() {
        if (localizationService == null) {
            localizationService = new LocalizationService(getSettings().getLanguage());
        }
        return localizationService;
    }
}
