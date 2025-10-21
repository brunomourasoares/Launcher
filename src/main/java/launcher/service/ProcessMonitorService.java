package launcher.service;

import launcher.model.ExecutableInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProcessMonitorService {
    private static final List<Process> runningProcesses = new CopyOnWriteArrayList<>();
    private static final int MONITORING_INTERVAL_SECONDS = 2;

    public static void terminateAll() {
        for (Process p : runningProcesses) {
            if (p.isAlive()) {
                p.destroyForcibly();
            }
        }
        runningProcesses.clear();
    }

    public static void executeAndMonitor(ExecutableInfo info, Runnable onStatusChange) {
        info.setStatus(ExecutableInfo.Status.EXECUTING);
        if (onStatusChange != null) onStatusChange.run();

        try {
            Process process = new ProcessBuilder(info.getPath()).start();
            runningProcesses.add(process);
            long pid = getProcessId(process);

            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(MONITORING_INTERVAL_SECONDS);

                    Set<Long> monitoredPids = new HashSet<>();
                    if (pid != -1) {
                        monitoredPids.add(pid);
                    }

                    if (process.isAlive()) {
                        info.setStatus(ExecutableInfo.Status.EXECUTED);
                        if (onStatusChange != null) onStatusChange.run();
                    }

                    while (true) {
                        if (pid != -1) {
                            monitoredPids.addAll(getChildPids(pid));
                        }

                        if (isAnyProcessRunning(info.getProcessNames(), monitoredPids)) {
                            if (info.getStatus() != ExecutableInfo.Status.EXECUTED) {
                                info.setStatus(ExecutableInfo.Status.EXECUTED);
                                if (onStatusChange != null) onStatusChange.run();
                            }
                        } else {
                            break;
                        }
                        TimeUnit.SECONDS.sleep(MONITORING_INTERVAL_SECONDS);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                } finally {
                    info.setStatus(ExecutableInfo.Status.CLOSED);
                    if (onStatusChange != null) onStatusChange.run();
                    runningProcesses.remove(process);
                }
            });
        } catch (Exception e) {
            info.setStatus(ExecutableInfo.Status.CLOSED);
            if (onStatusChange != null) onStatusChange.run();
        }
    }

    private static long getProcessId(Process process) {
        try {
            if (process.getClass().getName().equals("java.lang.Win32Process") ||
                process.getClass().getName().equals("java.lang.ProcessImpl")) {
                Field f = process.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handle = f.getLong(process);
            }
        } catch (Exception ignored) {}
        try {
            return (long) process.getClass().getMethod("pid").invoke(process);
        } catch (Exception ignored) {}
        return -1;
    }

    private static boolean isAnyProcessRunning(String[] exeNames, Set<Long> pids) {
        try {
            Process proc = Runtime.getRuntime().exec("tasklist /fo csv /nh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.replace('"', ' ').split(",");
                if (parts.length < 2) continue;
                String name = parts[0].trim();
                long currentPid = -1;
                try { currentPid = Long.parseLong(parts[1].trim()); } catch (Exception ignored) {}

                for (String exeName : exeNames) {
                    if (name.equalsIgnoreCase(exeName)) {
                        return true;
                    }
                }

                if (pids.contains(currentPid)) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static Set<Long> getChildPids(long parentPid) {
        Set<Long> childPids = new HashSet<>();
        try {
            Process proc = Runtime.getRuntime().exec("wmic process where (ParentProcessId=" + parentPid + ") get ProcessId");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.equalsIgnoreCase("ProcessId")) continue;
                try {
                    long pid = Long.parseLong(line);
                    childPids.add(pid);
                    childPids.addAll(getChildPids(pid));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return childPids;
    }
}