package org.aurora.launcher.diagnostic.performance;

import java.time.Instant;

public class PerformanceSample {
    private Instant timestamp;
    private MemoryUsage memory;
    private double cpuUsage;
    private int fps;
    private int threadCount;

    public PerformanceSample() {
        this.timestamp = Instant.now();
    }

    public PerformanceSample(Instant timestamp, MemoryUsage memory, double cpuUsage, int fps, int threadCount) {
        this.timestamp = timestamp;
        this.memory = memory;
        this.cpuUsage = cpuUsage;
        this.fps = fps;
        this.threadCount = threadCount;
    }

    public static PerformanceSample current() {
        MemoryUsage memory = MemoryUsage.current();
        int threadCount = Thread.activeCount();
        return new PerformanceSample(Instant.now(), memory, 0, 0, threadCount);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public MemoryUsage getMemory() {
        return memory;
    }

    public void setMemory(MemoryUsage memory) {
        this.memory = memory;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}