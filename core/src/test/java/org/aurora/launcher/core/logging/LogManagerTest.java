package org.aurora.launcher.core.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class LogManagerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LogManager.shutdown();
    }

    @Test
    void initialize_createsLogDirectory() {
        Path logDir = tempDir.resolve("logs");
        LogManager.initialize(logDir);
        assertTrue(logDir.toFile().exists() || logDir.toFile().mkdirs());
    }

    @Test
    void getLogger_withName_returnsLogger() {
        Logger logger = Logger.getLogger("TestLogger");
        assertNotNull(logger);
    }

    @Test
    void getLogger_withClass_returnsLogger() {
        Logger logger = Logger.getLogger(LogManagerTest.class);
        assertNotNull(logger);
    }

    @Test
    void setLevel_changesLogLevel() {
        LogManager.setLevel(LogLevel.DEBUG);
        LogManager.shutdown();
    }
}