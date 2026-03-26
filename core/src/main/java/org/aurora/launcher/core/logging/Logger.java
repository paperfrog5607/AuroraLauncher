package org.aurora.launcher.core.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
    private final String name;
    private final org.slf4j.Logger slf4jLogger;

    private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    Logger(String name) {
        this.name = name;
        this.slf4jLogger = LoggerFactory.getLogger(name);
    }

    public void debug(String message) {
        slf4jLogger.debug(message);
        appendLog(LogLevel.DEBUG, message, null);
    }

    public void info(String message) {
        slf4jLogger.info(message);
        appendLog(LogLevel.INFO, message, null);
    }

    public void warn(String message) {
        slf4jLogger.warn(message);
        appendLog(LogLevel.WARN, message, null);
    }

    public void error(String message) {
        slf4jLogger.error(message);
        appendLog(LogLevel.ERROR, message, null);
    }

    public void error(String message, Throwable t) {
        slf4jLogger.error(message, t);
        appendLog(LogLevel.ERROR, message, t);
    }

    private void appendLog(LogLevel level, String message, Throwable t) {
        List<LogAppender> appenders = LogManager.getAppenders();
        for (LogAppender appender : appenders) {
            appender.append(level, name, message, t);
        }
    }

    public static Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, Logger::new);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public String getName() {
        return name;
    }
}