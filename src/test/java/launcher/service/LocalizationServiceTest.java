package launcher.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocalizationServiceTest {
    @Test
    void testGetString_ExistingKey() {
        LocalizationService service = new LocalizationService("en");
        String value = service.getString("ui.app.title");
        assertNotNull(value);
        assertFalse(value.isEmpty());
    }

    @Test
    void testGetFormatted() {
        LocalizationService service = new LocalizationService("en");
        String pattern = service.getFormatted("test.app.greeting", "World");
        assertTrue(pattern.contains("World"));
    }

    @Test
    void testGetString_Portuguese() {
        LocalizationService service = new LocalizationService("pt");
        String value = service.getString("ui.app.title");
        assertNotNull(value);
        assertFalse(value.isEmpty());
    }

    @Test
    void testGetString_Spanish() {
        LocalizationService service = new LocalizationService("es");
        String value = service.getString("ui.app.title");
        assertNotNull(value);
        assertFalse(value.isEmpty());
    }
}
