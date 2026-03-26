package org.aurora.launcher.launcher.memory;

import java.util.List;

public class MemoryConfig {
    private final long initialHeap;
    private final long maxHeap;
    private final List<String> jvmArguments;

    public MemoryConfig(long initialHeap, long maxHeap, List<String> jvmArguments) {
        this.initialHeap = initialHeap;
        this.maxHeap = maxHeap;
        this.jvmArguments = jvmArguments;
    }

    public long getInitialHeap() {
        return initialHeap;
    }

    public long getMaxHeap() {
        return maxHeap;
    }

    public List<String> getJvmArguments() {
        return jvmArguments;
    }

    public String getInitialHeapArgument() {
        return "-Xms" + (initialHeap / (1024 * 1024)) + "M";
    }

    public String getMaxHeapArgument() {
        return "-Xmx" + (maxHeap / (1024 * 1024)) + "M";
    }

    public long getInitialHeapMB() {
        return initialHeap / (1024 * 1024);
    }

    public long getMaxHeapMB() {
        return maxHeap / (1024 * 1024);
    }
}