package org.aurora.launcher.launcher.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;

public class MemoryCalculator {
    private static final long MIN_MEMORY = 512L * 1024 * 1024;
    private static final long DEFAULT_MEMORY = 2L * 1024 * 1024 * 1024;

    public long getSystemMemory() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getTotalPhysicalMemorySize();
        }
        
        return DEFAULT_MEMORY;
    }

    public long getAvailableMemory() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getFreePhysicalMemorySize();
        }
        
        return DEFAULT_MEMORY / 2;
    }

    public long calculateRecommended(boolean hasShaders, boolean hasResourcePacks, int modCount) {
        long base = 1024L * 1024 * 1024;
        
        if (modCount > 0) {
            base += modCount * 50L * 1024 * 1024;
        }
        
        if (hasShaders) {
            base += 1024L * 1024 * 1024;
        }
        
        if (hasResourcePacks) {
            base += 512L * 1024 * 1024;
        }
        
        return Math.max(base, MIN_MEMORY);
    }
}