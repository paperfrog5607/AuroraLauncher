package org.aurora.launcher.diagnostic.fps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FpsChart {
    private List<Point> dataPoints;
    private double[] fpsBuckets;
    private Map<String, Double> averageByLocation;

    public static class Point {
        private long timestamp;
        private int fps;

        public Point(long timestamp, int fps) {
            this.timestamp = timestamp;
            this.fps = fps;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getFps() {
            return fps;
        }
    }

    public FpsChart() {
        this.dataPoints = new ArrayList<>();
        this.fpsBuckets = new double[10];
        this.averageByLocation = new HashMap<>();
    }

    public List<Point> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<Point> dataPoints) {
        this.dataPoints = dataPoints != null ? dataPoints : new ArrayList<>();
    }

    public void addPoint(Point point) {
        if (point != null) {
            dataPoints.add(point);
        }
    }

    public void addPoint(long timestamp, int fps) {
        addPoint(new Point(timestamp, fps));
    }

    public double[] getFpsBuckets() {
        return fpsBuckets;
    }

    public void setFpsBuckets(double[] fpsBuckets) {
        this.fpsBuckets = fpsBuckets;
    }

    public Map<String, Double> getAverageByLocation() {
        return averageByLocation;
    }

    public void setAverageByLocation(Map<String, Double> averageByLocation) {
        this.averageByLocation = averageByLocation != null ? averageByLocation : new HashMap<>();
    }

    public void setLocationAverage(String location, double average) {
        averageByLocation.put(location, average);
    }

    public static FpsChart fromLog(FpsLog log) {
        FpsChart chart = new FpsChart();
        
        if (log == null || log.getSamples().isEmpty()) {
            return chart;
        }
        
        List<FpsSample> samples = log.getSamples();
        
        for (FpsSample sample : samples) {
            chart.addPoint(
                sample.getTimestamp().toEpochMilli(),
                sample.getFps()
            );
            
            if (sample.getLocation() != null) {
                String loc = sample.getLocation();
                Map<String, List<Integer>> locationFps = new HashMap<>();
                locationFps.computeIfAbsent(loc, k -> new ArrayList<>()).add(sample.getFps());
            }
        }
        
        calculateBuckets(chart, samples);
        
        return chart;
    }

    private static void calculateBuckets(FpsChart chart, List<FpsSample> samples) {
        double[] buckets = new double[10];
        int[] counts = new int[10];
        
        for (FpsSample sample : samples) {
            int fps = sample.getFps();
            int bucket = Math.min(fps / 30, 9);
            buckets[bucket] += fps;
            counts[bucket]++;
        }
        
        for (int i = 0; i < 10; i++) {
            if (counts[i] > 0) {
                buckets[i] /= counts[i];
            }
        }
        
        chart.setFpsBuckets(buckets);
    }
}