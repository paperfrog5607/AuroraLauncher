package org.aurora.launcher.diagnostic.fps;

public class FpsStatistics {
    private double average;
    private int min;
    private int max;
    private double percentile1;
    private double percentile5;
    private double standardDeviation;

    public FpsStatistics() {
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public double getPercentile1() {
        return percentile1;
    }

    public void setPercentile1(double percentile1) {
        this.percentile1 = percentile1;
    }

    public double getPercentile5() {
        return percentile5;
    }

    public void setPercentile5(double percentile5) {
        this.percentile5 = percentile5;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public boolean isStable() {
        return standardDeviation < 10;
    }

    public String getPerformanceRating() {
        if (average >= 144) return "Excellent";
        if (average >= 60) return "Good";
        if (average >= 30) return "Playable";
        return "Poor";
    }
}