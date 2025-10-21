package launcher.ui;

import launcher.model.Settings;
import launcher.service.LocalizationService;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class SettingsDialogTest {
    @Test
    void testDialogInstantiation() {
        JFrame parent = new JFrame();
        Settings settings = new Settings("en", true, false, 30);
        LocalizationService loc = new LocalizationService("en");
        SettingsDialog dialog = new SettingsDialog(parent, settings, loc);
        assertNotNull(dialog);
        assertTrue(dialog.isModal());
    }

    @Test
    void testDialogFieldChange() {
        JFrame parent = new JFrame();
        Settings settings = new Settings("en", true, false, 30);
        LocalizationService loc = new LocalizationService("en");
        SettingsDialog dialog = new SettingsDialog(parent, settings, loc);
        dialog.setVisible(true);
        javax.swing.JTextField timeoutField = (javax.swing.JTextField) dialog.getContentPane().getComponent(5);
        timeoutField.setText("99");
        assertEquals("99", timeoutField.getText());
    }
}
