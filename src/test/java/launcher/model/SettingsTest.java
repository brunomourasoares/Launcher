package launcher.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @Test
    void testConstructorAndGetters() {
        Settings s = new Settings("pt", true, false);
        assertEquals("pt", s.getLanguage());
        assertTrue(s.isAutoStart());
        assertFalse(s.isSaveData());
        assertEquals(30, s.getTimeout());
        assertFalse(s.isRestartAllOnClose());
        assertEquals("light", s.getTheme());
    }

    @Test
    void testConstructorWithTimeout() {
        Settings s = new Settings("en", false, true, 99);
        assertEquals("en", s.getLanguage());
        assertFalse(s.isAutoStart());
        assertTrue(s.isSaveData());
        assertEquals(99, s.getTimeout());
        assertFalse(s.isRestartAllOnClose());
        assertEquals("light", s.getTheme());
    }

    @Test
    void testSetters() {
        Settings s = new Settings("es", false, false);
        s.setLanguage("en");
        s.setAutoStart(true);
        s.setSaveData(true);
        s.setTimeout(123);
        s.setRestartAllOnClose(true);
        s.setTheme("dark");
        assertEquals("en", s.getLanguage());
        assertTrue(s.isAutoStart());
        assertTrue(s.isSaveData());
        assertEquals(123, s.getTimeout());
        assertTrue(s.isRestartAllOnClose());
        assertEquals("dark", s.getTheme());
    }

    @Test
    void testSettersWithNullsAndLimits() {
        Settings s = new Settings("en", false, false);
        s.setLanguage(null);
        s.setTheme(null);
        s.setTimeout(-1);
        // Permite null e valores negativos
        assertNull(s.getLanguage());
        assertNull(s.getTheme());
        assertEquals(-1, s.getTimeout());
    }
}
