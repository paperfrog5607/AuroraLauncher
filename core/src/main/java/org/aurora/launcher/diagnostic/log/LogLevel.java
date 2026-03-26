package org.aurora.launcher.diagnostic.log;

public enum LogLevel {
    FATAL,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE;

    public static LogLevel fromString(String level) {
        if (level == null) return INFO;
        switch (level.toUpperCase()) {
            case "FATAL": return FATAL;
            case "ERROR": case "SEVERE": return ERROR;
            case "WARN": case "WARNING": return WARN;
            case "DEBUG": return DEBUG;
            case "TRACE": case "FINEST": return TRACE;
            default: return INFO;
        }
    }
}