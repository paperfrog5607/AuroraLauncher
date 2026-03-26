package org.aurora.launcher.diagnostic.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "\\[(\\d{2}:\\d{2}:\\d{2})\\]\\s*\\[([^\\]]+)\\]\\s*(?:\\[([^\\]]+)\\]\\s*)?(?:\\[([^\\]]+)\\]\\s*)?(.*)"
    );
    
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile(
        "^\\s*at\\s+[a-zA-Z0-9_.]+\\([^)]+\\)"
    );

    public List<LogEntry> parse(Path logFile) throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        
        if (logFile == null || !Files.exists(logFile)) {
            return entries;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            LogEntry currentEntry = null;
            StringBuilder throwableBuilder = null;
            
            while ((line = reader.readLine()) != null) {
                if (EXCEPTION_PATTERN.matcher(line).find() && currentEntry != null) {
                    if (throwableBuilder == null) {
                        throwableBuilder = new StringBuilder();
                    }
                    throwableBuilder.append(line).append("\n");
                } else {
                    if (currentEntry != null && throwableBuilder != null) {
                        currentEntry.setThrowable(throwableBuilder.toString());
                        throwableBuilder = null;
                    }
                    
                    LogEntry entry = parseLine(line);
                    if (entry != null) {
                        entries.add(entry);
                        currentEntry = entry;
                    }
                }
            }
            
            if (currentEntry != null && throwableBuilder != null) {
                currentEntry.setThrowable(throwableBuilder.toString());
            }
        }
        
        return entries;
    }

    public LogEntry parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            LogEntry entry = new LogEntry();
            
            String timeStr = matcher.group(1);
            entry.setTimestamp(parseTime(timeStr));
            
            String levelStr = matcher.group(2);
            entry.setLevel(LogLevel.fromString(levelStr));
            
            String group3 = matcher.group(3);
            String group4 = matcher.group(4);
            String message = matcher.group(5);
            
            if (group4 != null && !group4.isEmpty()) {
                entry.setThread(group3);
                entry.setLogger(group4);
            } else if (group3 != null && !group3.isEmpty()) {
                entry.setThread(group3);
            }
            
            entry.setMessage(message != null ? message : "");
            
            return entry;
        }
        
        return null;
    }

    private Instant parseTime(String timeStr) {
        if (timeStr == null) return Instant.now();
        
        try {
            String today = java.time.LocalDate.now().toString();
            return Instant.parse(today + "T" + timeStr + ":00Z");
        } catch (DateTimeParseException e) {
            return Instant.now();
        }
    }
}