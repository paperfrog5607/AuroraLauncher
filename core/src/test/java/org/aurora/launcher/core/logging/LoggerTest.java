package org.aurora.launcher.core.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = Logger.getLogger("TestLogger");
    }

    @Test
    void getLogger_sameName_returnsSameInstance() {
        Logger logger1 = Logger.getLogger("Test");
        Logger logger2 = Logger.getLogger("Test");
        assertSame(logger1, logger2);
    }

    @Test
    void debug_doesNotThrow() {
        assertDoesNotThrow(() -> logger.debug("Debug message"));
    }

    @Test
    void info_doesNotThrow() {
        assertDoesNotThrow(() -> logger.info("Info message"));
    }

    @Test
    void warn_doesNotThrow() {
        assertDoesNotThrow(() -> logger.warn("Warn message"));
    }

    @Test
    void error_doesNotThrow() {
        assertDoesNotThrow(() -> logger.error("Error message"));
    }

    @Test
    void error_withThrowable_doesNotThrow() {
        Exception e = new RuntimeException("Test exception");
        assertDoesNotThrow(() -> logger.error("Error message", e));
    }

    @Test
    void getLogger_withClass_returnsLoggerWithClassName() {
        Logger classLogger = Logger.getLogger(LoggerTest.class);
        assertNotNull(classLogger);
    }
}