package launcher.service;

import launcher.model.ExecutableInfo;
import launcher.model.Settings;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceServiceTest {
    private static final String EXEC_FILE = "executables.dat";
    private static final String SETTINGS_FILE = "settings.dat";

    @BeforeEach
    void beforeEach() {
        new File(EXEC_FILE).delete();
        new File(SETTINGS_FILE).delete();
    }

    @AfterEach
    void cleanup() {
        new File(EXEC_FILE).delete();
        new File(SETTINGS_FILE).delete();
    }

    @Test
    void testSaveAndLoadExecutables() throws Exception {
        List<ExecutableInfo> list = Arrays.asList(
            new ExecutableInfo("a.exe", 1, true),
            new ExecutableInfo("b.exe", 2, false)
        );
        PersistenceService.saveExecutables(list);
        List<ExecutableInfo> loaded = PersistenceService.loadExecutables();
        assertEquals(2, loaded.size());
        assertEquals("a.exe", loaded.get(0).getPath());
        assertEquals("b.exe", loaded.get(1).getPath());
    }

    @Test
    void testSaveAndLoadSettings() throws Exception {
        Settings s = new Settings("pt", true, false, 42);
        s.setTheme("dark");
        PersistenceService.saveSettings(s);
        Settings loaded = PersistenceService.loadSettings();
        assertEquals("pt", loaded.getLanguage());
        assertEquals(42, loaded.getTimeout());
        assertEquals("dark", loaded.getTheme());
    }

    @Test
    void testLoadExecutables_FileNotFound() {
        assertThrows(IOException.class, PersistenceService::loadExecutables);
    }

    @Test
    void testLoadSettings_FileNotFound() throws Exception {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.gc();
                Thread.sleep(500);
                deleted = file.delete();
            }
            assertTrue(deleted, "Não foi possível remover o arquivo settings.dat antes do teste! Caminho: " + file.getAbsolutePath());
        }
        assertFalse(file.exists(), "Arquivo settings.dat ainda existe antes do teste! Caminho: " + file.getAbsolutePath());
        assertThrows(IOException.class, PersistenceService::loadSettings);
    }

    @Test
    void testLoadExecutables_CorruptedFile() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(EXEC_FILE)) {
            fos.write(new byte[]{0,1,2,3,4,5});
        }
        assertThrows(IOException.class, PersistenceService::loadExecutables);
    }

    @Test
    void testLoadSettings_CorruptedFile() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            fos.write(new byte[]{9,8,7,6,5});
        }
        assertThrows(IOException.class, PersistenceService::loadSettings);
    }
}
