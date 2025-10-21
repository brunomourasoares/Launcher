package launcher.ui;

import launcher.model.ExecutableInfo;
import launcher.service.PersistenceService;
import launcher.service.ProcessMonitorService;
import launcher.model.Settings;
import launcher.service.LocalizationService;
import launcher.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

public class MainFrame extends JFrame {
    private final JPanel blocksPanel;
    private final JButton addBlockButton;
    private final JButton startButton;
    private final JButton settingsButton;
    private final JButton helpButton;
    private final List<ExecutableBlockPanel> blockPanels;

    private final Settings settings;
    private LocalizationService localization;

    private final JTextField timeoutField;
    private final JTextArea logArea;
    private final JCheckBox saveSwitch;
    private final JCheckBox autoStartSwitch;
    private final JCheckBox restartAllOnCloseSwitch;

    private boolean isExecuting = false;
    private final MainFrameController controller;

    private void handleError(String message) {
        JOptionPane.showMessageDialog(this,
            localization.getString("ui.config.theme.error"),
            localization.getString("ui.window.error"),
            JOptionPane.ERROR_MESSAGE);
    }

    public MainFrame(MainFrameController controller) {
        this.controller = controller;
        this.localization = controller.getLocalization();
        this.settings = controller.getSettings();

        int defaultTimeout = 30;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("launcher/config.properties")) {
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                String timeoutStr = prop.getProperty("config.defaultTimeout");
                if (timeoutStr != null) {
                    try {
                        defaultTimeout = Integer.parseInt(timeoutStr);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception ignored) {}

        Settings defaultSettings = new Settings("pt", false, false, defaultTimeout);
        Settings loadedSettings;
        try {
            loadedSettings = PersistenceService.loadSettings();
        } catch (Exception e) {
            loadedSettings = defaultSettings;
        }
        this.settings.setLanguage(loadedSettings.getLanguage());
        this.settings.setTimeout(loadedSettings.getTimeout());
        this.settings.setAutoStart(loadedSettings.isAutoStart());
        this.settings.setSaveData(loadedSettings.isSaveData());
        this.settings.setRestartAllOnClose(loadedSettings.isRestartAllOnClose());
        this.settings.setTheme(loadedSettings.getTheme());

        try {
            if (settings.getTheme() != null && settings.getTheme().equals("dark")) {
                com.formdev.flatlaf.FlatDarkLaf.setup();
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } else {
                com.formdev.flatlaf.FlatLightLaf.setup();
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            }
        } catch (Exception e) {
            handleError(e.getMessage());
        }

        setTitle(localization.getString("ui.app.title"));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (blockPanels.isEmpty()) {
                    saveSwitch.setSelected(false);
                    autoStartSwitch.setSelected(false);
                    settings.setSaveData(false);
                    settings.setAutoStart(false);
                    saveSettings();
                }
                int result = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    localization.getString("ui.exit.confirm"),
                    localization.getString("ui.exit.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        setSize(1280, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            java.net.URL iconURL = getClass().getClassLoader().getResource("launcher/launcher_icon.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            handleError(e.getMessage());
        }

        blockPanels = new ArrayList<>();
        blocksPanel = new JPanel();
        blocksPanel.setLayout(new GridLayout(3, 2, 10, 10));
        JScrollPane scrollPane = new JScrollPane(blocksPanel);

        addBlockButton = new JButton(localization.getString("ui.button.addExecutable"));
        startButton = new JButton(localization.getString("ui.button.startExecution"));
        settingsButton = new JButton();
        helpButton = new JButton(localization.getString("ui.button.help"));
        timeoutField = new JTextField(String.valueOf(settings.getTimeout()), 5);

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        AppLogger.init(logArea, localization);

        saveSwitch = new JCheckBox();
        autoStartSwitch = new JCheckBox();
        restartAllOnCloseSwitch = new JCheckBox();
        restartAllOnCloseSwitch.setSelected(settings.isRestartAllOnClose());
        restartAllOnCloseSwitch.addActionListener(e -> {
            settings.setRestartAllOnClose(restartAllOnCloseSwitch.isSelected());
            saveSettings();
        });

        saveSwitch.addActionListener(e -> {
            settings.setSaveData(saveSwitch.isSelected());
            saveSettings();
            if (!saveSwitch.isSelected()) {
                if (autoStartSwitch.isSelected()) {
                    autoStartSwitch.setSelected(false);
                    settings.setAutoStart(false);
                }
                try {
                    PersistenceService.saveExecutables(new ArrayList<>());
                } catch (IOException ex) {
                    AppLogger.clearBlocksError(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            localization.getString("log.block.clearBlocksError"),
                            localization.getString("ui.window.error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                saveBlocks();
            }
        });
        autoStartSwitch.addActionListener(e -> {
            if (autoStartSwitch.isSelected()) {
                saveSwitch.setSelected(true);
                settings.setSaveData(true);
            }
            settings.setAutoStart(autoStartSwitch.isSelected());
            saveSettings();
        });


        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(addBlockButton);
        leftPanel.add(startButton);
        leftPanel.add(saveSwitch);
        leftPanel.add(autoStartSwitch);
        leftPanel.add(restartAllOnCloseSwitch);
        topPanel.add(leftPanel, BorderLayout.WEST);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(settingsButton);
        rightPanel.add(helpButton);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);

        addBlockButton.addActionListener(e -> addBlock());
        startButton.addActionListener(e -> startExecution());
        settingsButton.addActionListener(e -> {
            try {
                SettingsDialog dialog = new SettingsDialog(this, settings, localization);
                dialog.setVisible(true);
                saveSettings();
                localization = new LocalizationService(settings.getLanguage());
                AppLogger.updateLocalization(localization);
                AppLogger.init(logArea, localization);
                updateTexts();
            } catch (Exception ex) {
                AppLogger.saveSettingsError(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        localization.getString("log.block.saveSettingsError"),
                        localization.getString("ui.window.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        Properties helpConfig = new Properties();
        String emailUrl = "", phoneUrl = "";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("launcher/config.properties")) {
            if (in != null) {
                helpConfig.load(in);
                emailUrl = helpConfig.getProperty("help.email.url", "");
                phoneUrl = helpConfig.getProperty("help.phone.url", "");
            }
        } catch (IOException | NumberFormatException ignored) { }
        final String finalEmailUrl = emailUrl, finalPhoneUrl = phoneUrl;

        helpButton.addActionListener(e -> {
            String name = localization.getString("ui.help.name");
            String email = localization.getString("ui.help.email");
            String phone = localization.getString("ui.help.phone");

            // Detectar tema atual
            Color linkColor = UIManager.getColor("Label.foreground");
            String colorHex = String.format("#%02x%02x%02x", linkColor.getRed(), linkColor.getGreen(), linkColor.getBlue());

            String emailLink = finalEmailUrl.isEmpty() ? email : "<a href='" + finalEmailUrl + "' style='color:" + colorHex + ";text-decoration:none;'>" + email + "</a>";
            String phoneLink = finalPhoneUrl.isEmpty() ? phone : "<a href='" + finalPhoneUrl + "' style='color:" + colorHex + ";text-decoration:none;'>" + phone + "</a>";
            String message = String.format("<html>%s<br>%s<br>%s</html>", name, emailLink, phoneLink);

            JLabel label = new JLabel(message);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    try {
                        if (evt.getY() < label.getFontMetrics(label.getFont()).getHeight() * 2) {
                            if (!finalEmailUrl.isEmpty()) Desktop.getDesktop().browse(new URI(finalEmailUrl));
                        } else if (!finalPhoneUrl.isEmpty()) {
                            Desktop.getDesktop().browse(new URI(finalPhoneUrl));
                        }
                    } catch (Exception ignored) { }
                }
            });

            JDialog dialog = new JOptionPane(label, JOptionPane.INFORMATION_MESSAGE)
                    .createDialog(MainFrame.this, localization.getString("ui.help.title"));
            dialog.setModal(false);
            dialog.setVisible(true);
        });

        controller.addPropertyChangeListener(evt -> {
            if ("executables".equals(evt.getPropertyName())) {
                updateBlocksFromController();
            }
        });

        loadData();
        updateTexts();

        JPopupMenu logMenu = new JPopupMenu();
        JMenuItem saveLogItem = new JMenuItem(localization.getString("ui.log.mouse.saveLog"));
        JMenuItem clearLogItem = new JMenuItem(localization.getString("ui.log.mouse.clearLog"));
        logMenu.add(saveLogItem);
        logMenu.add(clearLogItem);

        logArea.setComponentPopupMenu(logMenu);

        saveLogItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(localization.getString("ui.window.save.saveLog.title"));
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(localization.getString("ui.window.save.saveLog.description"), localization.getString("ui.window.save.saveLog.format")));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith("." + localization.getString("ui.window.save.saveLog.format"))) {
                    file = new java.io.File(file.getAbsolutePath() + "." + localization.getString("ui.window.save.saveLog.format"));
                }
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(logArea.getText());
                    JOptionPane.showMessageDialog(this,
                            localization.getString("ui.window.information.saveLog.success"),
                            localization.getString("ui.window.information"),
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            localization.getString("ui.window.save.saveLog.error") + ex.getMessage(),
                            localization.getString("ui.window.warning"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearLogItem.addActionListener(e -> AppLogger.clear());
    }

    private void addBlock() {
        if (blockPanels.size() >= 6) {
            AppLogger.info("log.block.maxBlocksReached");
            JOptionPane.showMessageDialog(this,
                    localization.getString("ui.window.warning.maxBlocksReached"),
                    localization.getString("ui.window.warning"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        ExecutableInfo info = new ExecutableInfo("", 5, true);
        controller.addExecutable(info);
        // Não adicionar manualmente à blockPanels ou blocksPanel!
        AppLogger.created(info.getPath());
        saveBlocks();
    }

    private void updateBlocksPanelLayout() {
        blocksPanel.removeAll();
        blocksPanel.setLayout(new GridLayout(3, 2, 10, 10));
        for (ExecutableBlockPanel panel : blockPanels) {
            blocksPanel.add(panel);
        }
        for (int i = blockPanels.size(); i < 6; i++) {
            blocksPanel.add(Box.createGlue());
        }
        blocksPanel.revalidate();
        blocksPanel.repaint();
    }

    private void startExecution() {
        if (isExecuting) return;
        isExecuting = true;

        // Encerra todos os processos da execução anterior
        ProcessMonitorService.terminateAll();

        saveBlocks();
        boolean hasActive = false;
        for (ExecutableBlockPanel panel : blockPanels) {
            panel.updateStatus(ExecutableInfo.Status.NOT_EXECUTED);
            if (panel.getExecutableInfo().isEnabled()) {
                hasActive = true;
            }
        }
        if (blockPanels.isEmpty() || !hasActive) {
            AppLogger.info("log.block.noActiveExecutable");
            JOptionPane.showMessageDialog(this,
                    localization.getString("ui.window.warning.noBlockAdded"),
                    localization.getString("ui.window.warning"),
                    JOptionPane.WARNING_MESSAGE);
            isExecuting = false;
            return;
        }

        // A lista runningProcesses foi removida daqui para centralizar no ProcessMonitorService
        for (ExecutableBlockPanel panel : blockPanels) {
            ExecutableInfo info = panel.getExecutableInfo();
            if (info.isEnabled() && panel.isExecutablePathValid()) {
                AppLogger.info("log.block.invalidExecutableOrDisable");
                JOptionPane.showMessageDialog(this,
                        localization.getString("ui.window.warning.executableNotSelected"),
                        localization.getString("ui.window.warning"),
                        JOptionPane.WARNING_MESSAGE);
                isExecuting = false;
                return;
            }
        }
        new Thread(() -> {
            long timeout = settings.getTimeout() * 1000L;
            long startTime = System.currentTimeMillis();
            for (ExecutableBlockPanel panel : blockPanels) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    AppLogger.warning(localization.getString("log.block.timeout"));
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        localization.getString("ui.window.openExecutable.timeout"),
                        localization.getString("ui.window.warning"),
                        JOptionPane.WARNING_MESSAGE));
                    break;
                }
                panel.showPathValidation();
                ExecutableInfo info = panel.getExecutableInfo();
                if (!info.isEnabled() || panel.isExecutablePathValid()) continue;
                SwingUtilities.invokeLater(() -> panel.updateStatus(ExecutableInfo.Status.EXECUTING));
                AppLogger.waitDelay(info.getPath());
                try {
                    Thread.sleep(info.getDelay() * 1000L);
                    ProcessMonitorService.executeAndMonitor(info, () -> {
                        SwingUtilities.invokeLater(() -> panel.updateStatus(info.getStatus()));
                        if (info.getStatus() == ExecutableInfo.Status.CLOSED) {
                            AppLogger.closed(info.getPath());
                            if (settings.isRestartAllOnClose()) {
                                restartAllExecutables();
                            }
                        } else if (info.getStatus() == ExecutableInfo.Status.EXECUTING) {
                            AppLogger.opened(info.getPath());
                            AppLogger.monitoring(String.join(",", info.getProcessNames()));
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> panel.updateStatus(ExecutableInfo.Status.NOT_EXECUTED));
                    AppLogger.runError(ex.getMessage());
                }
            }
            isExecuting = false;
        }).start();
    }


    private void saveSettings() {
        controller.saveSettings();
    }

    private void saveBlocks() {
        if (!settings.isSaveData()) return;
        controller.saveExecutables();
    }

    private void clearBlocks() {
        blockPanels.clear();
        blocksPanel.removeAll();
        blocksPanel.revalidate();
        blocksPanel.repaint();
        try {
            PersistenceService.saveExecutables(new ArrayList<>());
        } catch (IOException e) {
            AppLogger.clearBlocksError(e.getMessage());
            JOptionPane.showMessageDialog(this,
                    localization.getString("log.block.clearBlocksError"),
                    localization.getString("ui.window.warning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        try {
            Settings loadedSettings = PersistenceService.loadSettings();
            if (loadedSettings != null) {
                settings.setLanguage(loadedSettings.getLanguage());
                settings.setAutoStart(loadedSettings.isAutoStart());
                settings.setSaveData(loadedSettings.isSaveData());
                settings.setTimeout(loadedSettings.getTimeout());
                settings.setRestartAllOnClose(loadedSettings.isRestartAllOnClose());
            }
        } catch (Exception ignored) {
            AppLogger.noConfig();
            JOptionPane.showMessageDialog(this,
                    localization.getString("log.config.noConfig"),
                    localization.getString("ui.window.warning"),
                    JOptionPane.ERROR_MESSAGE);
        }

        localization = new LocalizationService(settings.getLanguage());
        AppLogger.init(logArea, localization);

        if (settings.isSaveData()) {
            saveSwitch.setSelected(true);
            try {
                List<ExecutableInfo> infos = PersistenceService.loadExecutables();
                for (ExecutableInfo info : infos) {
                    final ExecutableBlockPanel[] panelRef = new ExecutableBlockPanel[1];
                    panelRef[0] = new ExecutableBlockPanel(info, localization, () -> {
                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                localization.getString("ui.window.information.removeExecutableBlock"),
                                localization.getString("ui.window.information"),
                                JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            controller.removeExecutable(info);
                            // Não manipular blockPanels diretamente!
                            saveBlocks();
                        }
                    });
                    panelRef[0].setBlockEnabled(info.isEnabled()); // Garante que o bloco fique travado/destravado corretamente
                    panelRef[0].addChangeListener(() -> {
                        if (saveSwitch.isSelected()) {
                            saveBlocks();
                        }
                    });
                    blockPanels.add(panelRef[0]);
                    panelRef[0].setDelay(info.getDelay());
                }
                updateBlocksPanelLayout();
            } catch (Exception ignored) {
                AppLogger.noSavedBlocks();
                JOptionPane.showMessageDialog(this,
                        localization.getString("log.noSavedBlocks"),
                        localization.getString("ui.window.warning"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            clearBlocks();
        }

        if (settings.isAutoStart()) {
            autoStartSwitch.setSelected(true);
            startExecution();
        }
    }

    private void updateTexts() {
        SwingUtilities.updateComponentTreeUI(this);
        setTitle(localization.getString("ui.app.title"));
        addBlockButton.setText(localization.getString("ui.button.addExecutable"));
        startButton.setText(localization.getString("ui.button.startExecution"));
        settingsButton.setText(localization.getString("ui.button.settings"));
        helpButton.setText(localization.getString("ui.button.help"));
        saveSwitch.setText(localization.getString("ui.switch.saveData"));
        autoStartSwitch.setText(localization.getString("ui.switch.autoStart"));
        restartAllOnCloseSwitch.setText(localization.getString("ui.switch.restartAllOnClose"));
        restartAllOnCloseSwitch.setSelected(settings.isRestartAllOnClose());
        timeoutField.setToolTipText(localization.getString("ui.tooltip.timeout"));
        for (ExecutableBlockPanel panel : blockPanels) {
            panel.updateLocalization(localization);
        }
        if (blockPanels.size() >= 6) {
            AppLogger.info("log.block.maxBlocksReached");
        }
    }

    private void restartAllExecutables() {
        // Encerra todos os processos antes de reiniciar
        ProcessMonitorService.terminateAll();
        SwingUtilities.invokeLater(this::startExecution);
    }

    private void updateSwitchesState() {
        boolean hasBlocks = !blockPanels.isEmpty();
        saveSwitch.setEnabled(hasBlocks);
        autoStartSwitch.setEnabled(hasBlocks);
        if (!hasBlocks) {
            saveSwitch.setSelected(false);
            autoStartSwitch.setSelected(false);
            settings.setSaveData(false);
            settings.setAutoStart(false);
            saveSettings();
        }
    }

    private void updateBlocksFromController() {
        blockPanels.clear();
        blocksPanel.removeAll();
        for (ExecutableInfo info : controller.getExecutables()) {
            ExecutableBlockPanel panel = new ExecutableBlockPanel(info, localization, null);
            blockPanels.add(panel);
            blocksPanel.add(panel);
        }
        updateBlocksPanelLayout();
        blocksPanel.revalidate();
        blocksPanel.repaint();
    }
}
