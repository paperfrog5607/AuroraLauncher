package org.aurora.launcher.core.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogManager {
    private static final List<LogAppender> appenders = new CopyOnWriteArrayList<>();
    private static LogLevel currentLevel = LogLevel.INFO;
    private static Path logDir;
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    private LogManager() {
    }

    public static void initialize(Path logDirectory) {
        logDir = logDirectory;
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + logDir);
        }
        addAppender(new ConsoleAppender());
    }

    public static void setLevel(LogLevel level) {
        currentLevel = level;
    }

    public static LogLevel getLevel() {
        return currentLevel;
    }

    public static void addAppender(LogAppender appender) {
        appenders.add(appender);
    }

    public static void removeAppender(LogAppender appender) {
        appenders.remove(appender);
    }

    public static List<LogAppender> getAppenders() {
        return new ArrayList<>(appenders);
    }

    public static void shutdown() {
        appenders.clear();
    }

    private static class ConsoleAppender implements LogAppender {
        @Override
        public void append(LogLevel level, String name, String message, Throwable throwable) {
            StringBuilder sb = new StringBuilder();
            sb.append(formatter.format(Instant.now()))
              .append(" [").append(level).append("] ")
              .append(name).append(" - ")
              .append(message);
            
            System.out.println(sb.toString());
            
            if (throwable != null) {
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                System.out.println(sw.toString());
            }
        }
    }
}