package launcher.ui;

import launcher.model.ExecutableInfo;
import launcher.service.LocalizationService;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class ExecutableBlockPanelTest {
    @Test
    void testPanelInstantiation() {
        ExecutableInfo info = new ExecutableInfo("C:/test.exe", 5, true);
        LocalizationService loc = new LocalizationService("en");
        ExecutableBlockPanel panel = new ExecutableBlockPanel(info, loc);
        assertNotNull(panel);
        assertEquals(500, panel.getPreferredSize().width);
    }

    @Test
    void testPathFieldChange() {
        ExecutableInfo info = new ExecutableInfo("C:/test.exe", 5, true);
        LocalizationService loc = new LocalizationService("en");
        ExecutableBlockPanel panel = new ExecutableBlockPanel(info, loc);
        panel.setVisible(true);
        javax.swing.JTextField pathField = null;
        for (java.awt.Component c : panel.getComponents()) {
            if (c instanceof JTextField) {
                pathField = (JTextField) c;
                break;
            }
        }
        assertNotNull(pathField);
        pathField.setText("C:/new.exe");
        assertEquals("C:/new.exe", pathField.getText());
    }
}
