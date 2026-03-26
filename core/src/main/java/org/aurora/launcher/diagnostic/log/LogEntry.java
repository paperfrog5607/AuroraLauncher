package org.aurora.launcher.diagnostic.log;

import java.time.Instant;

public class LogEntry {
    private Instant timestamp;
    private LogLevel level;
    private String thread;
    private String logger;
    private String message;
    private String throwable;

    public LogEntry() {
    }

    public LogEntry(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThrowable() {
        return throwable;
    }

    public void setThrowable(String throwable) {
        this.throwable = throwable;
    }

    public boolean isError() {
        return level == LogLevel.ERROR || level == LogLevel.FATAL;
    }

    public boolean isWarning() {
        return level == LogLevel.WARN;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (timestamp != null) sb.append("[").append(timestamp).append("] ");
        if (level != null) sb.append("[").append(level).append("] ");
        if (thread != null) sb.append("[").append(thread).append("] ");
        if (logger != null) sb.append("[").append(logger).append("] ");
        if (message != null) sb.append(message);
        return sb.toString();
    }
}