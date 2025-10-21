package launcher.ui;

import launcher.model.Settings;
import launcher.service.LocalizationService;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class MainFrameTest {
    @Test
    void testMainFrameInstantiation() {
        Settings settings = new Settings("pt", false, false, 30);
        LocalizationService localization = new LocalizationService("pt");
        MainFrameController controller = new MainFrameController(settings, localization);
        MainFrame frame = new MainFrame(controller);
        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
    }

    @Test
    void testMainFrameAddBlockButton() {
        Settings settings = new Settings("pt", false, false, 30);
        LocalizationService localization = new LocalizationService("pt");
        MainFrameController controller = new MainFrameController(settings, localization);
        MainFrame frame = new MainFrame(controller);
        frame.setVisible(true);
        JButton addBlockButton = findButton(frame.getContentPane(), "addBlockButton");
        assertNotNull(addBlockButton);
        assertTrue(addBlockButton.isEnabled());
    }

    private JButton findButton(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton) {
                return (JButton) c;
            } else if (c instanceof Container) {
                JButton btn = findButton((Container) c, name);
                if (btn != null) return btn;
            }
        }
        return null;
    }
}
