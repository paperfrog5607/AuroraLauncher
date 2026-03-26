package org.aurora.launcher.diagnostic.performance;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PerformanceMonitor {
    private final ScheduledExecutorService scheduler;
    private final List<PerformanceSample> samples;
    private final AtomicBoolean monitoring;
    private final int maxSamples;
    private int fpsThreshold = 30;
    private double memoryThreshold = 90;
    private double cpuThreshold = 80;

    public PerformanceMonitor() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.samples = new ArrayList<>();
        this.monitoring = new AtomicBoolean(false);
        this.maxSamples = 3600;
    }

    public PerformanceMonitor(int maxSamples) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.samples = new ArrayList<>();
        this.monitoring = new AtomicBoolean(false);
        this.maxSamples = maxSamples;
    }

    public void start(Duration interval) {
        if (monitoring.compareAndSet(false, true)) {
            samples.clear();
            scheduler.scheduleAtFixedRate(
                this::sample,
                0,
                interval.toMillis(),
                TimeUnit.MILLISECONDS
            );
        }
    }

    public void stop() {
        if (monitoring.compareAndSet(true, false)) {
            scheduler.shutdown();
        }
    }

    public boolean isMonitoring() {
        return monitoring.get();
    }

    private void sample() {
        PerformanceSample sample = new PerformanceSample();
        sample.setMemory(MemoryUsage.current());
        sample.setCpuUsage(getCpuUsage());
        sample.setThreadCount(Thread.activeCount());
        
        synchronized (samples) {
            samples.add(sample);
            if (samples.size() > maxSamples) {
                samples.remove(0);
            }
        }
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getProcessCpuLoad() * 100;
        }
        return osBean.getSystemLoadAverage();
    }

    public PerformanceReport generateReport(Duration period) {
        PerformanceReport report = new PerformanceReport();
        
        List<PerformanceSample> samplesCopy;
        synchronized (samples) {
            Instant cutoff = Instant.now().minus(period);
            samplesCopy = new ArrayList<>();
            for (PerformanceSample s : samples) {
                if (s.getTimestamp().isAfter(cutoff)) {
                    samplesCopy.add(s);
                }
            }
        }
        
        if (samplesCopy.isEmpty()) {
            return report;
        }
        
        report.setSampleCount(samplesCopy.size());
        report.setDuration(period);
        
        double totalFps = 0;
        int minFps = Integer.MAX_VALUE;
        int maxFps = 0;
        long totalMemory = 0;
        long peakMemory = 0;
        double totalCpu = 0;
        double peakCpu = 0;
        
        for (PerformanceSample sample : samplesCopy) {
            int fps = sample.getFps();
            if (fps > 0) {
                totalFps += fps;
                minFps = Math.min(minFps, fps);
                maxFps = Math.max(maxFps, fps);
            }
            
            if (sample.getMemory() != null) {
                long mem = sample.getMemory().getHeapUsed();
                totalMemory += mem;
                peakMemory = Math.max(peakMemory, mem);
            }
            
            totalCpu += sample.getCpuUsage();
            peakCpu = Math.max(peakCpu, sample.getCpuUsage());
        }
        
        int fpsSamples = (int) samplesCopy.stream().filter(s -> s.getFps() > 0).count();
        if (fpsSamples > 0) {
            report.setAverageFps(totalFps / fpsSamples);
            report.setMinFps(minFps == Integer.MAX_VALUE ? 0 : minFps);
            report.setMaxFps(maxFps);
        }
        
        report.setAverageMemoryUsage(totalMemory / samplesCopy.size());
        report.setPeakMemoryUsage(peakMemory);
        report.setAverageCpuUsage(totalCpu / samplesCopy.size());
        report.setPeakCpuUsage(peakCpu);
        
        detectIssues(report, samplesCopy);
        
        return report;
    }

    private void detectIssues(PerformanceReport report, List<PerformanceSample> samplesCopy) {
        if (report.getMinFps() > 0 && report.getMinFps() < fpsThreshold) {
            report.addIssue(new PerformanceIssue(
                PerformanceIssue.IssueType.LOW_FPS,
                "FPS dropped below " + fpsThreshold + " (min: " + report.getMinFps() + ")",
                PerformanceIssue.Severity.WARNING
            ));
        }
        
        if (report.getPeakMemoryUsage() > 0) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            double usagePercent = (report.getPeakMemoryUsage() * 100.0) / maxMemory;
            if (usagePercent > memoryThreshold) {
                report.addIssue(new PerformanceIssue(
                    PerformanceIssue.IssueType.HIGH_MEMORY,
                    "Memory usage exceeded " + memoryThreshold + "%",
                    PerformanceIssue.Severity.WARNING
                ));
            }
        }
        
        if (report.getPeakCpuUsage() > cpuThreshold) {
            report.addIssue(new PerformanceIssue(
                PerformanceIssue.IssueType.HIGH_CPU,
                "CPU usage exceeded " + cpuThreshold + "%",
                PerformanceIssue.Severity.WARNING
            ));
        }
    }

    public List<PerformanceSample> getSamples() {
        synchronized (samples) {
            return new ArrayList<>(samples);
        }
    }

    public void clearSamples() {
        synchronized (samples) {
            samples.clear();
        }
    }

    public int getFpsThreshold() {
        return fpsThreshold;
    }

    public void setFpsThreshold(int fpsThreshold) {
        this.fpsThreshold = fpsThreshold;
    }

    public double getMemoryThreshold() {
        return memoryThreshold;
    }

    public void setMemoryThreshold(double memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

    public double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }
}