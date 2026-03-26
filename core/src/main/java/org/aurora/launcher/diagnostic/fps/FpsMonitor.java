package org.aurora.launcher.diagnostic.fps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FpsMonitor {
    private final Map<String, FpsLog> activeLogs;
    private final List<FpsLog> completedLogs;

    public FpsMonitor() {
        this.activeLogs = new ConcurrentHashMap<>();
        this.completedLogs = Collections.synchronizedList(new ArrayList<>());
    }

    public void startMonitoring(String instanceId) {
        FpsLog log = new FpsLog(instanceId);
        activeLogs.put(instanceId, log);
    }

    public void recordFps(String instanceId, int fps) {
        FpsLog log = activeLogs.get(instanceId);
        if (log != null) {
            log.addSample(fps);
        }
    }

    public void recordFps(String instanceId, int fps, String location) {
        FpsLog log = activeLogs.get(instanceId);
        if (log != null) {
            FpsSample sample = new FpsSample(fps);
            sample.setLocation(location);
            log.addSample(sample);
        }
    }

    public void stopMonitoring(String instanceId) {
        FpsLog log = activeLogs.remove(instanceId);
        if (log != null) {
            log.end();
            log.setStatistics(calculateStatistics(log));
            completedLogs.add(log);
        }
    }

    public FpsLog getCurrentLog(String instanceId) {
        return activeLogs.get(instanceId);
    }

    public List<FpsLog> getLogs(String instanceId) {
        List<FpsLog> result = new ArrayList<>();
        synchronized (completedLogs) {
            for (FpsLog log : completedLogs) {
                if (instanceId == null || instanceId.equals(log.getInstanceId())) {
                    result.add(log);
                }
            }
        }
        return result;
    }

    public FpsChart generateChart(FpsLog log) {
        return FpsChart.fromLog(log);
    }

    public FpsChart generateChart(String instanceId) {
        FpsLog log = getCurrentLog(instanceId);
        if (log != null) {
            return generateChart(log);
        }
        return new FpsChart();
    }

    public FpsStatistics calculateStatistics(FpsLog log) {
        FpsStatistics stats = new FpsStatistics();
        
        if (log == null || log.getSamples().isEmpty()) {
            return stats;
        }
        
        List<FpsSample> samples = log.getSamples();
        List<Integer> fpsValues = new ArrayList<>();
        
        int min = Integer.MAX_VALUE;
        int max = 0;
        double sum = 0;
        
        for (FpsSample sample : samples) {
            int fps = sample.getFps();
            fpsValues.add(fps);
            sum += fps;
            min = Math.min(min, fps);
            max = Math.max(max, fps);
        }
        
        double average = sum / samples.size();
        
        Collections.sort(fpsValues);
        
        double p1 = getPercentile(fpsValues, 1);
        double p5 = getPercentile(fpsValues, 5);
        
        double variance = 0;
        for (int fps : fpsValues) {
            variance += Math.pow(fps - average, 2);
        }
        double stdDev = Math.sqrt(variance / fpsValues.size());
        
        stats.setAverage(average);
        stats.setMin(min == Integer.MAX_VALUE ? 0 : min);
        stats.setMax(max);
        stats.setPercentile1(p1);
        stats.setPercentile5(p5);
        stats.setStandardDeviation(stdDev);
        
        return stats;
    }

    private double getPercentile(List<Integer> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) return 0;
        
        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        
        if (lower == upper) {
            return sortedValues.get(lower);
        }
        
        double weight = index - lower;
        return sortedValues.get(lower) * (1 - weight) + sortedValues.get(upper) * weight;
    }

    public boolean isMonitoring(String instanceId) {
        return activeLogs.containsKey(instanceId);
    }

    public void clearCompletedLogs() {
        completedLogs.clear();
    }
}