package org.aurora.launcher.core.platform;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlatformTest {

    @Test
    void getOS_returnsNonNull() {
        Platform.OS os = Platform.getOS();
        assertNotNull(os);
    }

    @Test
    void getArch_returnsNonNull() {
        Platform.Arch arch = Platform.getArch();
        assertNotNull(arch);
    }

    @Test
    void getOSVersion_returnsNonNull() {
        String version = Platform.getOSVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    void getJavaVersion_returnsNonNull() {
        String version = Platform.getJavaVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    void getJavaHome_returnsNonNull() {
        String javaHome = Platform.getJavaHome();
        assertNotNull(javaHome);
        assertFalse(javaHome.isEmpty());
    }

    @Test
    void getTotalMemory_returnsPositiveValue() {
        long memory = Platform.getTotalMemory();
        assertTrue(memory > 0);
    }

    @Test
    void getAvailableMemory_returnsPositiveValue() {
        long memory = Platform.getAvailableMemory();
        assertTrue(memory >= 0);
    }

    @Test
    void getWorkingDirectory_returnsNonNull() {
        String workDir = Platform.getWorkingDirectory();
        assertNotNull(workDir);
        assertFalse(workDir.isEmpty());
    }

    @Test
    void getAppDataDirectory_returnsNonNull() {
        java.nio.file.Path appData = Platform.getAppDataDirectory();
        assertNotNull(appData);
    }
}