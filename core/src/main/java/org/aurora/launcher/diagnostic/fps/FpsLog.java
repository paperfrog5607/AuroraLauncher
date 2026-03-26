package org.aurora.launcher.diagnostic.fps;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FpsLog {
    private String instanceId;
    private Instant startTime;
    private Instant endTime;
    private List<FpsSample> samples;
    private FpsStatistics statistics;

    public FpsLog() {
        this.samples = new ArrayList<>();
    }

    public FpsLog(String instanceId) {
        this.instanceId = instanceId;
        this.samples = new ArrayList<>();
        this.startTime = Instant.now();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<FpsSample> getSamples() {
        return samples;
    }

    public void setSamples(List<FpsSample> samples) {
        this.samples = samples != null ? samples : new ArrayList<>();
    }

    public void addSample(FpsSample sample) {
        if (sample != null) {
            samples.add(sample);
        }
    }

    public void addSample(int fps) {
        addSample(new FpsSample(fps));
    }

    public FpsStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(FpsStatistics statistics) {
        this.statistics = statistics;
    }

    public void end() {
        this.endTime = Instant.now();
    }

    public int getSampleCount() {
        return samples.size();
    }

    public long getDurationSeconds() {
        if (startTime == null || endTime == null) return 0;
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }
}