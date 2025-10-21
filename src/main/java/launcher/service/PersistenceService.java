package launcher.service;

import launcher.model.ExecutableInfo;
import launcher.model.Settings;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

public class PersistenceService {
    private static final String EXECUTABLES_FILE = "executables.dat";
    private static final String SETTINGS_FILE = "settings.dat";

    public static void saveExecutables(List<ExecutableInfo> executables) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXECUTABLES_FILE))) {
            oos.writeObject(new ArrayList<>(executables));
        }
    }

    public static List<ExecutableInfo> loadExecutables() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXECUTABLES_FILE))) {
            return (List<ExecutableInfo>) ois.readObject();
        }
    }

    public static void saveSettings(Settings settings) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE))) {
            oos.writeObject(settings);
        }
    }

    public static Settings loadSettings() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SETTINGS_FILE))) {
            return (Settings) ois.readObject();
        }
    }
}

