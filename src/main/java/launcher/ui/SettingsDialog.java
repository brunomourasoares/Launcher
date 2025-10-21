package launcher.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import launcher.model.Settings;
import launcher.service.LocalizationService;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private final JComboBox<String> languageCombo;
    private final JComboBox<String> themeCombo;
    private final JTextField timeoutField;

    public SettingsDialog(JFrame parent, Settings settings, LocalizationService localization) {
        super(parent, localization.getString("ui.button.settings"), true);
        this.timeoutField = new JTextField(String.valueOf(settings.getTimeout()), 5);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] languageCodes = {"pt", "en", "es"};
        String[] languageNames = {
                localization.getString("ui.config.language.pt"),
                localization.getString("ui.config.language.en"),
                localization.getString("ui.config.language.es")
        };
        languageCombo = new JComboBox<>(languageNames);
        int selectedIndex = 0;
        if (settings.getLanguage().equals("en")) {
            selectedIndex = 1;
        } else if (settings.getLanguage().equals("es")) {
            selectedIndex = 2;
        }
        languageCombo.setSelectedIndex(selectedIndex);

        themeCombo = new JComboBox<>(new String[]{
            localization.getString("ui.config.theme.light"),
            localization.getString("ui.config.theme.dark")
        });
        themeCombo.setSelectedIndex(settings.getTheme().equals("dark") ? 1 : 0);

        JButton okButton = new JButton(localization.getString("ui.button.config.ok"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel(localization.getString("ui.config.language")), gbc);
        gbc.gridx = 1;
        add(languageCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel(localization.getString("ui.config.theme")), gbc);
        gbc.gridx = 1;
        add(themeCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel(localization.getString("ui.config.timeout")), gbc);
        gbc.gridx = 1;
        add(timeoutField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(okButton, gbc);

        okButton.addActionListener(e -> {
            settings.setLanguage(languageCodes[languageCombo.getSelectedIndex()]);
            settings.setTheme(themeCombo.getSelectedIndex() == 1 ? "dark" : "light");
            try {
                if (themeCombo.getSelectedIndex() == 0) {
                    FlatLightLaf.setup();
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } else {
                    FlatDarkLaf.setup();
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                }
                SwingUtilities.updateComponentTreeUI(parent);
                SwingUtilities.updateComponentTreeUI(this);
                settings.setTimeout(Integer.parseInt(timeoutField.getText()));
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, localization.getString("ui.window.warning.invalidTimeout"), localization.getString("ui.window.warning"), JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, localization.getString("ui.config.theme.error") + ex.getMessage(), localization.getString("ui.window.warning"), JOptionPane.ERROR_MESSAGE);
            }
        });

        pack();
        setLocationRelativeTo(parent);
    }
}
