package launcher.service;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationService {
    private final ResourceBundle bundle;

    public LocalizationService(String language) {
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("launcher.lang", locale);
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public String getFormatted(String key, Object... params) {
        String pattern = getString(key);
        return java.text.MessageFormat.format(pattern, params);
    }
}
