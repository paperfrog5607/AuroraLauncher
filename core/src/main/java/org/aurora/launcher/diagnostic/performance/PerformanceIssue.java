package org.aurora.launcher.diagnostic.performance;

import java.time.Instant;

public class PerformanceIssue {
    public enum IssueType {
        LOW_FPS,
        HIGH_MEMORY,
        MEMORY_LEAK,
        HIGH_CPU,
        GC_PAUSE
    }

    public enum Severity {
        INFO,
        WARNING,
        CRITICAL
    }

    private IssueType type;
    private String description;
    private Instant timestamp;
    private Severity severity;

    public PerformanceIssue() {
        this.timestamp = Instant.now();
    }

    public PerformanceIssue(IssueType type, String description, Severity severity) {
        this.type = type;
        this.description = description;
        this.timestamp = Instant.now();
        this.severity = severity;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}