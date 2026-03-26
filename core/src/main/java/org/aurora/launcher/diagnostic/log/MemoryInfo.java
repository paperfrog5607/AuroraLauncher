package org.aurora.launcher.diagnostic.log;

public class MemoryInfo {
    private long totalMemory;
    private long freeMemory;
    private long maxMemory;
    private long usedMemory;

    public MemoryInfo() {
    }

    public MemoryInfo(long totalMemory, long freeMemory, long maxMemory) {
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
        this.maxMemory = maxMemory;
        this.usedMemory = totalMemory - freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public double getUsagePercent() {
        if (maxMemory <= 0) return 0;
        return (usedMemory * 100.0) / maxMemory;
    }

    public long getUsedMemoryMB() {
        return usedMemory / (1024 * 1024);
    }

    public long getMaxMemoryMB() {
        return maxMemory / (1024 * 1024);
    }
}