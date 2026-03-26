package org.aurora.launcher.diagnostic.performance;

public class MemoryUsage {
    private long heapUsed;
    private long heapMax;
    private long nonHeapUsed;

    public MemoryUsage() {
    }

    public MemoryUsage(long heapUsed, long heapMax, long nonHeapUsed) {
        this.heapUsed = heapUsed;
        this.heapMax = heapMax;
        this.nonHeapUsed = nonHeapUsed;
    }

    public static MemoryUsage current() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long max = runtime.maxMemory();
        return new MemoryUsage(total - free, max, 0);
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public double getUsagePercent() {
        if (heapMax <= 0) return 0;
        return (heapUsed * 100.0) / heapMax;
    }

    public long getHeapUsedMB() {
        return heapUsed / (1024 * 1024);
    }

    public long getHeapMaxMB() {
        return heapMax / (1024 * 1024);
    }
}