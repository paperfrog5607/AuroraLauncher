package org.aurora.launcher.core.logging;

public interface LogAppender {
    void append(LogLevel level, String name, String message, Throwable throwable);
}