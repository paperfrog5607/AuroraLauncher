package org.aurora.launcher.diagnostic.performance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PerformanceReport {
    private Duration duration;
    private int sampleCount;
    private double averageFps;
    private int minFps;
    private int maxFps;
    private double averageMemoryUsage;
    private long peakMemoryUsage;
    private double averageCpuUsage;
    private double peakCpuUsage;
    private List<PerformanceIssue> issues;

    public PerformanceReport() {
        this.issues = new ArrayList<>();
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public double getAverageFps() {
        return averageFps;
    }

    public void setAverageFps(double averageFps) {
        this.averageFps = averageFps;
    }

    public int getMinFps() {
        return minFps;
    }

    public void setMinFps(int minFps) {
        this.minFps = minFps;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public double getAverageMemoryUsage() {
        return averageMemoryUsage;
    }

    public void setAverageMemoryUsage(double averageMemoryUsage) {
        this.averageMemoryUsage = averageMemoryUsage;
    }

    public long getPeakMemoryUsage() {
        return peakMemoryUsage;
    }

    public void setPeakMemoryUsage(long peakMemoryUsage) {
        this.peakMemoryUsage = peakMemoryUsage;
    }

    public double getAverageCpuUsage() {
        return averageCpuUsage;
    }

    public void setAverageCpuUsage(double averageCpuUsage) {
        this.averageCpuUsage = averageCpuUsage;
    }

    public double getPeakCpuUsage() {
        return peakCpuUsage;
    }

    public void setPeakCpuUsage(double peakCpuUsage) {
        this.peakCpuUsage = peakCpuUsage;
    }

    public List<PerformanceIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<PerformanceIssue> issues) {
        this.issues = issues != null ? issues : new ArrayList<>();
    }

    public void addIssue(PerformanceIssue issue) {
        if (issue != null) {
            issues.add(issue);
        }
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public boolean hasCriticalIssues() {
        return issues.stream()
            .anyMatch(i -> i.getSeverity() == PerformanceIssue.Severity.CRITICAL);
    }
}