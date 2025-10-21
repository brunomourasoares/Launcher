package launcher;

import launcher.ui.MainFrame;
import launcher.ui.MainFrameController;
import javax.swing.*;

public class LauncherApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        MainFrameController controller = new MainFrameController(Injector.getSettings(), Injector.getLocalizationService());
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}