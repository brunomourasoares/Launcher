package launcher.service;

import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import java.io.OutputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import launcher.model.ExecutableInfo;

class ProcessMonitorServiceTest {
    static class DummyProcess extends Process {
        private boolean alive = true;
        @Override public boolean isAlive() { return alive; }
        @Override public Process destroyForcibly() { alive = false; return this; }
        public OutputStream getOutputStream() { return null; }
        public InputStream getInputStream() { return null; }
        public InputStream getErrorStream() { return null; }
        public int waitFor() { return 0; }
        public int exitValue() { return 0; }
        public void destroy() { alive = false; }
    }

    @BeforeEach
    void clearRunningProcesses() throws Exception {
        Field f = ProcessMonitorService.class.getDeclaredField("runningProcesses");
        f.setAccessible(true);
        ((List<?>) f.get(null)).clear();
    }

    @Test
    void testTerminateAll() throws Exception {
        Field f = ProcessMonitorService.class.getDeclaredField("runningProcesses");
        f.setAccessible(true);
        List<Process> list = (List<Process>) f.get(null);
        DummyProcess p1 = new DummyProcess();
        DummyProcess p2 = new DummyProcess();
        list.add(p1);
        list.add(p2);
        ProcessMonitorService.terminateAll();
        assertFalse(p1.isAlive());
        assertFalse(p2.isAlive());
        assertTrue(list.isEmpty());
    }

    @Test
    void testExecuteAndMonitor_StatusChangeAndCallback() throws Exception {
        ExecutableInfo info = new ExecutableInfo("cmd.exe", 0, true);
        final boolean[] called = {false};
        Runnable callback = () -> called[0] = true;
        ProcessMonitorService.executeAndMonitor(info, callback);
        assertEquals(ExecutableInfo.Status.EXECUTING, info.getStatus());
        assertTrue(called[0]);
    }
}
