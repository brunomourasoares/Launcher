package launcher.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExecutableInfoTest {
    @Test
    void testConstructorAndGetters() {
        ExecutableInfo info = new ExecutableInfo("C:/Program Files/App/app.exe", 10, true);
        assertEquals("C:/Program Files/App/app.exe", info.getPath());
        assertEquals(10, info.getDelay());
        assertTrue(info.isEnabled());
        assertEquals(ExecutableInfo.Status.NOT_EXECUTED, info.getStatus());
    }

    @Test
    void testSetters() {
        ExecutableInfo info = new ExecutableInfo("a.exe", 1, false);
        info.setPath("b.exe");
        info.setDelay(5);
        info.setEnabled(true);
        info.setStatus(ExecutableInfo.Status.EXECUTED);
        assertEquals("b.exe", info.getPath());
        assertEquals(5, info.getDelay());
        assertTrue(info.isEnabled());
        assertEquals(ExecutableInfo.Status.EXECUTED, info.getStatus());
    }

    @Test
    void testSettersWithNulls() {
        ExecutableInfo info = new ExecutableInfo("a.exe", 1, true);
        info.setPath(null);
        info.setStatus(null);
        info.setProcessName(null);
        assertNull(info.getPath());
        assertNull(info.getStatus());
        assertThrows(NullPointerException.class, info::getProcessNames);
    }

    @Test
    void testGetProcessNames_Default() {
        ExecutableInfo info = new ExecutableInfo("C:/folder/test.exe", 0, true);
        String[] names = info.getProcessNames();
        assertEquals(1, names.length);
        assertEquals("test.exe", names[0]);
    }

    @Test
    void testGetProcessNames_WithCustomNames() {
        ExecutableInfo info = new ExecutableInfo("C:/folder/test.exe", 0, true);
        info.setProcessName("proc1.exe, proc2.exe");
        String[] names = info.getProcessNames();
        assertArrayEquals(new String[]{"proc1.exe", "proc2.exe"}, names);
    }

    @Test
    void testGetProcessNames_TrimSpaces() {
        ExecutableInfo info = new ExecutableInfo("C:/folder/test.exe", 0, true);
        info.setProcessName("  a.exe , b.exe  ,c.exe ");
        String[] names = info.getProcessNames();
        assertArrayEquals(new String[]{"  a.exe", "b.exe", "c.exe "}, names);
    }

    @Test
    void testSetDelayNegative() {
        ExecutableInfo info = new ExecutableInfo("a.exe", 1, true);
        info.setDelay(-10);
        assertEquals(-10, info.getDelay());
    }
}
