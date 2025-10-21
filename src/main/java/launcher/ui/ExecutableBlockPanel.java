package launcher.ui;

import launcher.model.ExecutableInfo;
import launcher.service.LocalizationService;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExecutableBlockPanel extends JPanel {
    private final JTextField pathField;
    private final JButton browseButton;
    private final JLabel statusLabel;
    private final JSpinner delaySpinner;
    private final JCheckBox enableSwitch;
    private final ExecutableInfo info;
    private LocalizationService localization;
    private final JLabel executableLabel;
    private final JLabel delayLabel;
    private final JLabel statusTextLabel;
    private final JButton removeButton;
    private final List<Runnable> changeListeners = new ArrayList<>();


    public ExecutableBlockPanel(ExecutableInfo info, LocalizationService localization) {
        this(info, localization, null);
    }

    public ExecutableBlockPanel(ExecutableInfo info, LocalizationService localization, Runnable onRemove) {
        this.info = info;
        this.localization = localization;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12,12,12,12),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
        ));

        setPreferredSize(new Dimension(500, 120));
        setMaximumSize(new Dimension(500, 120));
        setMinimumSize(new Dimension(500, 120));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        pathField = new JTextField(info.getPath(), 20);
        pathField.setEditable(false);
        browseButton = new JButton("...");
        removeButton = new JButton("✖");
        removeButton.setToolTipText(localization.getString("ui.tooltip.removeBlock"));
        statusLabel = new JLabel(getStatusText(info.getStatus()));
        delaySpinner = new JSpinner(new SpinnerNumberModel(info.getDelay(), 0, 3600, 1));
        enableSwitch = new JCheckBox(localization.getString("ui.switch.enabled"), info.isEnabled());

        pathField.setToolTipText(localization.getString("ui.tooltip.executablePath"));
        browseButton.setToolTipText(localization.getString("ui.tooltip.browseExecutable"));
        delaySpinner.setToolTipText(localization.getString("ui.tooltip.delay"));
        enableSwitch.setToolTipText(localization.getString("ui.tooltip.enableBlock"));
        statusLabel.setToolTipText(localization.getString("ui.tooltip.status"));

        executableLabel = new JLabel(localization.getString("ui.block.label.executable"));
        delayLabel = new JLabel(localization.getString("ui.block.label.delay"));
        statusTextLabel = new JLabel(localization.getString("ui.block.label.status"));

        gbc.gridx = 0; gbc.gridy = 0; add(executableLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; add(pathField, gbc);
        gbc.gridx = 2; add(browseButton, gbc);
        gbc.gridx = 3; add(removeButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(delayLabel, gbc);
        gbc.gridx = 1; add(delaySpinner, gbc);
        gbc.gridx = 2; add(enableSwitch, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(statusTextLabel, gbc);
        gbc.gridx = 1; add(statusLabel, gbc);

        browseButton.addActionListener(e -> browseExecutable());
        enableSwitch.addActionListener(e -> setBlockEnabled(enableSwitch.isSelected()));
        removeButton.addActionListener(e -> {
            if (onRemove != null) onRemove.run();
        });

        pathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { notifyChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { notifyChange(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { notifyChange(); }
        });

        delaySpinner.addChangeListener(e -> notifyChange());
        enableSwitch.addActionListener(e -> notifyChange());
        browseButton.addActionListener(e -> notifyChange());
        removeButton.addActionListener(e -> notifyChange());
    }

    private String getStatusText(ExecutableInfo.Status status) {
        return switch (status) {
            case NOT_EXECUTED -> localization.getString("ui.block.status.notExecuted");
            case EXECUTING -> localization.getString("ui.block.status.executing");
            case EXECUTED -> localization.getString("ui.block.status.executed");
            case CLOSED -> localization.getString("ui.block.status.closed");
        };
    }

    public ExecutableInfo getExecutableInfo() {
        info.setPath(pathField.getText());
        info.setDelay((Integer) delaySpinner.getValue());
        info.setEnabled(enableSwitch.isSelected());
        return info;
    }

    public void updateStatus(ExecutableInfo.Status status) {
        info.setStatus(status);
        statusLabel.setText(getStatusText(status));
        switch (status) {
            case NOT_EXECUTED:
                statusLabel.setForeground(Color.GRAY);
                break;
            case EXECUTING:
                statusLabel.setForeground(new Color(255, 165, 0)); // Orange
                break;
            case EXECUTED:
                statusLabel.setForeground(new Color(0, 128, 0)); // Green
                break;
            case CLOSED:
                statusLabel.setForeground(Color.RED);
                break;
            default:
                statusLabel.setForeground(Color.BLACK);
        }
    }

    private void browseExecutable() {
        String currentPath = pathField.getText();
        JFileChooser chooser;
        if (currentPath != null && !currentPath.isEmpty()) {
            File currentFile = new File(currentPath);
            File dir = currentFile.isDirectory() ? currentFile : currentFile.getParentFile();
            chooser = new JFileChooser(dir);
        } else {
            chooser = new JFileChooser();
        }
        chooser.setDialogTitle(localization.getString("ui.window.open.executable.title"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".exe");
            }
            public String getDescription() {
                return localization.getString("ui.window.open.executable.description");
            }
        });
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            pathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    void setBlockEnabled(boolean enabled) {
        pathField.setEnabled(enabled);
        browseButton.setEnabled(enabled);
        delaySpinner.setEnabled(enabled);
        statusLabel.setEnabled(enabled);
        notifyChangeListeners();
    }

    public boolean isExecutablePathValid() {
        String path = pathField.getText();
        return path == null || !path.toLowerCase().endsWith(".exe") || !new File(path).exists();
    }

    public void showPathValidation() {
        boolean isDark = UIManager.getLookAndFeel().getName().toLowerCase().contains("dark");
        Color bg = isDark ? UIManager.getColor("TextField.background") : Color.WHITE;
        if (bg == null && isDark) bg = new Color(60, 63, 65);

        pathField.setBackground(bg);

        if (isExecutablePathValid()) {
            pathField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 2),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));
            pathField.setToolTipText(localization.getString("ui.tooltip.invalidPathChooseExecutable"));
        } else {
            pathField.setBorder(BorderFactory.createCompoundBorder(
                    UIManager.getBorder("TextField.border"),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));
            pathField.setToolTipText(localization.getString("ui.tooltip.executablePath"));
        }

        pathField.setMinimumSize(new Dimension(300, 28));
    }

    public void updateLocalization(LocalizationService localization) {
        this.localization = localization;
        executableLabel.setText(localization.getString("ui.block.label.executable"));
        delayLabel.setText(localization.getString("ui.block.label.delay"));
        statusTextLabel.setText(localization.getString("ui.block.label.status"));
        enableSwitch.setText(localization.getString("ui.switch.enabled"));
        enableSwitch.setToolTipText(localization.getString("ui.tooltip.enableBlock"));
        pathField.setToolTipText(localization.getString("ui.tooltip.executablePath"));
        browseButton.setToolTipText(localization.getString("ui.tooltip.browseExecutable"));
        delaySpinner.setToolTipText(localization.getString("ui.tooltip.delay"));
        statusLabel.setToolTipText(localization.getString("ui.tooltip.status"));
        statusLabel.setText(getStatusText(info.getStatus()));
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyChange() {
        for (Runnable l : changeListeners) l.run();
    }

    private void notifyChangeListeners() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    public void setDelay(int delay) {
        delaySpinner.setValue(delay);
    }
}