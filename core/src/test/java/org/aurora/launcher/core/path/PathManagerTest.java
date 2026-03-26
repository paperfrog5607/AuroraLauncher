package org.aurora.launcher.core.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class PathManagerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        PathManager.reset();
        PathManager.initialize(tempDir);
    }

    @Test
    void initialize_createsInstance() {
        assertNotNull(PathManager.getInstance());
    }

    @Test
    void getBaseDirectory_returnsInitializedPath() {
        assertEquals(tempDir, PathManager.getInstance().getBaseDirectory());
    }

    @Test
    void getInstancesDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("instances");
        assertEquals(expected, PathManager.getInstance().getInstancesDirectory());
    }

    @Test
    void getInstanceDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("instances").resolve("my-instance");
        assertEquals(expected, PathManager.getInstance().getInstanceDirectory("my-instance"));
    }

    @Test
    void getCacheDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("cache");
        assertEquals(expected, PathManager.getInstance().getCacheDirectory());
    }

    @Test
    void getLogsDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("logs");
        assertEquals(expected, PathManager.getInstance().getLogsDirectory());
    }

    @Test
    void getConfigDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("config");
        assertEquals(expected, PathManager.getInstance().getConfigDirectory());
    }

    @Test
    void getTempDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("temp");
        assertEquals(expected, PathManager.getInstance().getTempDirectory());
    }

    @Test
    void getJavaDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("java");
        assertEquals(expected, PathManager.getInstance().getJavaDirectory());
    }

    @Test
    void getVersionsDirectory_returnsCorrectPath() {
        Path expected = tempDir.resolve("versions");
        assertEquals(expected, PathManager.getInstance().getVersionsDirectory());
    }
}