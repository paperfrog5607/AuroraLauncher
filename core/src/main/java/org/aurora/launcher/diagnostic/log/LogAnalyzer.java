package org.aurora.launcher.diagnostic.log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {
    private final LogParser parser;

    public LogAnalyzer() {
        this.parser = new LogParser();
    }

    public LogAnalysisResult analyze(Path logFile) throws IOException {
        LogAnalysisResult result = new LogAnalysisResult();
        
        List<LogEntry> entries = parser.parse(logFile);
        
        for (LogEntry entry : entries) {
            if (entry.isError()) {
                result.addError(entry);
            } else if (entry.isWarning()) {
                result.addWarning(entry);
            }
            
            extractModInfo(entry, result);
        }
        
        extractMemoryInfo(entries, result);
        extractJavaInfo(entries, result);
        generateRecommendations(result);
        
        return result;
    }

    public LogAnalysisResult analyze(String logContent) {
        LogAnalysisResult result = new LogAnalysisResult();
        
        if (logContent == null || logContent.isEmpty()) {
            return result;
        }
        
        String[] lines = logContent.split("\n");
        for (String line : lines) {
            LogEntry entry = parser.parseLine(line);
            if (entry != null) {
                if (entry.isError()) {
                    result.addError(entry);
                } else if (entry.isWarning()) {
                    result.addWarning(entry);
                }
                extractModInfo(entry, result);
            }
        }
        
        generateRecommendations(result);
        
        return result;
    }

    private void extractModInfo(LogEntry entry, LogAnalysisResult result) {
        String message = entry.getMessage();
        if (message == null) return;
        
        Pattern loadPattern = Pattern.compile("Loading mod\\s+([a-zA-Z0-9_-]+)", 
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = loadPattern.matcher(message);
        if (matcher.find()) {
            result.addLoadedMod(matcher.group(1));
        }
        
        Pattern failPattern = Pattern.compile(
            "Failed to load mod\\s+([a-zA-Z0-9_-]+)|Mod\\s+([a-zA-Z0-9_-]+)\\s+failed",
            Pattern.CASE_INSENSITIVE
        );
        matcher = failPattern.matcher(message);
        if (matcher.find()) {
            String modId = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            result.addFailedMod(modId);
        }
    }

    private void extractMemoryInfo(List<LogEntry> entries, LogAnalysisResult result) {
        for (LogEntry entry : entries) {
            String message = entry.getMessage();
            if (message != null && message.contains("memory")) {
                Pattern memoryPattern = Pattern.compile(
                    "(\\d+)\\s*MB\\s*(?:of|/)\\s*(\\d+)\\s*MB",
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = memoryPattern.matcher(message);
                if (matcher.find()) {
                    long used = Long.parseLong(matcher.group(1)) * 1024 * 1024;
                    long max = Long.parseLong(matcher.group(2)) * 1024 * 1024;
                    result.setMemoryInfo(new MemoryInfo(max, max - used, max));
                    break;
                }
            }
        }
    }

    private void extractJavaInfo(List<LogEntry> entries, LogAnalysisResult result) {
        for (LogEntry entry : entries) {
            String message = entry.getMessage();
            if (message != null && message.contains("Java")) {
                Pattern javaPattern = Pattern.compile(
                    "Java[\\s\\w]*([0-9]+(?:\\.[0-9]+)*)",
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = javaPattern.matcher(message);
                if (matcher.find()) {
                    JavaInfo javaInfo = new JavaInfo(matcher.group(1), "Unknown");
                    result.setJavaInfo(javaInfo);
                    break;
                }
            }
        }
    }

    private void generateRecommendations(LogAnalysisResult result) {
        if (result.getErrorCount() > 10) {
            result.addRecommendation("High number of errors detected. Consider checking mod compatibility.");
        }
        
        if (!result.getFailedMods().isEmpty()) {
            result.addRecommendation("Some mods failed to load. Check the logs for details.");
        }
        
        if (result.getMemoryInfo() != null && 
            result.getMemoryInfo().getUsagePercent() > 80) {
            result.addRecommendation("Memory usage is high. Consider allocating more RAM.");
        }
    }
}