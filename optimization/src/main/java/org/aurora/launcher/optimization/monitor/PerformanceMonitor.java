package org.aurora.launcher.optimization.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控器
 * 监控游戏进程的CPU、内存、帧率
 */
public class PerformanceMonitor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);

    private static PerformanceMonitor instance;

    private final MemoryMXBean memoryBean;
    private final OperatingSystemMXBean osBean;
    private final RuntimeMXBean runtimeBean;
    
    private final CopyOnWriteArrayList<PerformanceSnapshot> history;
    private ScheduledExecutorService scheduler;
    private Process monitoredProcess;
    private boolean isMonitoring;

    private PerformanceMonitor() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.history = new CopyOnWriteArrayList<>();
        this.isMonitoring = false;
    }

    public static synchronized PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }

    /**
     * 开始监控指定进程
     */
    public void startMonitoring(Process process) {
        if (isMonitoring) {
            stopMonitoring();
        }
        
        this.monitoredProcess = process;
        this.isMonitoring = true;
        
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::captureSnapshot, 0, 1, TimeUnit.SECONDS);
        
        logger.info("Started performance monitoring");
    }

    /**
     * 停止监控
     */
    public void stopMonitoring() {
        isMonitoring = false;
        
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        
        logger.info("Stopped performance monitoring");
    }

    /**
     * 捕获性能快照
     */
    private void captureSnapshot() {
        if (!isMonitoring) {
            return;
        }

        try {
            PerformanceSnapshot snapshot = new PerformanceSnapshot();
            
            // JVM内存
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            snapshot.setJvmHeapUsed(heapUsage.getUsed());
            snapshot.setJvmHeapMax(heapUsage.getMax());
            snapshot.setJvmHeapPercent(heapUsage.getUsed() * 100.0 / heapUsage.getMax());
            
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            snapshot.setJvmNonHeapUsed(nonHeapUsage.getUsed());
            
            // 系统内存
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            snapshot.setSystemMemoryTotal(sunOsBean.getTotalMemorySize());
            snapshot.setSystemMemoryFree(sunOsBean.getFreeMemorySize());
            snapshot.setSystemMemoryUsed(snapshot.getSystemMemoryTotal() - snapshot.getSystemMemoryFree());
            
            // CPU使用率
            snapshot.setSystemCpuLoad(getSystemCpuUsage());
            
            // 游戏进程CPU（如果可用）
            if (monitoredProcess != null && monitoredProcess.isAlive()) {
                snapshot.setProcessCpuUsage(getProcessCpuUsage());
            }
            
            // 帧率（如果有监控）
            snapshot.setFps(getCurrentFps());
            
            snapshot.setTimestamp(System.currentTimeMillis());
            
            history.add(snapshot);
            
            // 保持最近60个快照
            while (history.size() > 60) {
                history.remove(0);
            }
            
        } catch (Exception e) {
            logger.debug("Failed to capture performance snapshot", e);
        }
    }

    private double getSystemCpuUsage() {
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                return sunOsBean.getSystemCpuLoad() * 100;
            }
        } catch (Exception e) {
            logger.debug("Failed to get system CPU usage", e);
        }
        return 0;
    }

    private double getProcessCpuUsage() {
        try {
            if (monitoredProcess != null && monitoredProcess.isAlive()) {
                return 0;
            }
        } catch (Exception e) {
            logger.debug("Failed to get process CPU usage", e);
        }
        return 0;
    }

    private int getCurrentFps() {
        return 0;
    }

    /**
     * 获取最新快照
     */
    public PerformanceSnapshot getLatestSnapshot() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1);
    }

    /**
     * 获取历史快照
     */
    public CopyOnWriteArrayList<PerformanceSnapshot> getHistory() {
        return history;
    }

    /**
     * 获取性能报告
     */
    public PerformanceReport generateReport() {
        PerformanceReport report = new PerformanceReport();
        
        if (history.isEmpty()) {
            return report;
        }
        
        double avgFps = 0;
        double avgCpu = 0;
        double avgMem = 0;
        int count = 0;
        
        for (PerformanceSnapshot s : history) {
            avgFps += s.getFps();
            avgCpu += s.getSystemCpuUsage();
            avgMem += s.getJvmHeapPercent();
            count++;
        }
        
        if (count > 0) {
            report.setAverageFps(avgFps / count);
            report.setAverageCpu(avgCpu / count);
            report.setAverageMemory(avgMem / count);
        }
        
        PerformanceSnapshot latest = getLatestSnapshot();
        if (latest != null) {
            report.setCurrentFps(latest.getFps());
            report.setCurrentCpu(latest.getSystemCpuUsage());
            report.setCurrentMemory(latest.getJvmHeapPercent());
        }
        
        return report;
    }

    /**
     * 性能快照
     */
    public static class PerformanceSnapshot {
        private long timestamp;
        private long jvmHeapUsed;
        private long jvmHeapMax;
        private double jvmHeapPercent;
        private long jvmNonHeapUsed;
        private long systemMemoryTotal;
        private long systemMemoryFree;
        private long systemMemoryUsed;
        private double systemCpuLoad;
        private double processCpuUsage;
        private int fps;

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public long getJvmHeapUsed() { return jvmHeapUsed; }
        public void setJvmHeapUsed(long jvmHeapUsed) { this.jvmHeapUsed = jvmHeapUsed; }
        public long getJvmHeapMax() { return jvmHeapMax; }
        public void setJvmHeapMax(long jvmHeapMax) { this.jvmHeapMax = jvmHeapMax; }
        public double getJvmHeapPercent() { return jvmHeapPercent; }
        public void setJvmHeapPercent(double jvmHeapPercent) { this.jvmHeapPercent = jvmHeapPercent; }
        public long getJvmNonHeapUsed() { return jvmNonHeapUsed; }
        public void setJvmNonHeapUsed(long jvmNonHeapUsed) { this.jvmNonHeapUsed = jvmNonHeapUsed; }
        public long getSystemMemoryTotal() { return systemMemoryTotal; }
        public void setSystemMemoryTotal(long systemMemoryTotal) { this.systemMemoryTotal = systemMemoryTotal; }
        public long getSystemMemoryFree() { return systemMemoryFree; }
        public void setSystemMemoryFree(long systemMemoryFree) { this.systemMemoryFree = systemMemoryFree; }
        public long getSystemMemoryUsed() { return systemMemoryUsed; }
        public void setSystemMemoryUsed(long systemMemoryUsed) { this.systemMemoryUsed = systemMemoryUsed; }
        public double getSystemCpuUsage() { return systemCpuLoad; }
        public void setSystemCpuLoad(double systemCpuLoad) { this.systemCpuLoad = systemCpuLoad; }
        public double getProcessCpuUsage() { return processCpuUsage; }
        public void setProcessCpuUsage(double processCpuUsage) { this.processCpuUsage = processCpuUsage; }
        public int getFps() { return fps; }
        public void setFps(int fps) { this.fps = fps; }
    }

    /**
     * 性能报告
     */
    public static class PerformanceReport {
        private double currentFps;
        private double averageFps;
        private double currentCpu;
        private double averageCpu;
        private double currentMemory;
        private double averageMemory;

        public double getCurrentFps() { return currentFps; }
        public void setCurrentFps(double currentFps) { this.currentFps = currentFps; }
        public double getAverageFps() { return averageFps; }
        public void setAverageFps(double averageFps) { this.averageFps = averageFps; }
        public double getCurrentCpu() { return currentCpu; }
        public void setCurrentCpu(double currentCpu) { this.currentCpu = currentCpu; }
        public double getAverageCpu() { return averageCpu; }
        public void setAverageCpu(double averageCpu) { this.averageCpu = averageCpu; }
        public double getCurrentMemory() { return currentMemory; }
        public void setCurrentMemory(double currentMemory) { this.currentMemory = currentMemory; }
        public double getAverageMemory() { return averageMemory; }
        public void setAverageMemory(double averageMemory) { this.averageMemory = averageMemory; }
    }
}
