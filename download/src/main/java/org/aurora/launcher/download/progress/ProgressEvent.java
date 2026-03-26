package org.aurora.launcher.download.progress;

import java.time.Duration;

public class ProgressEvent {
    private final long downloadedBytes;
    private final long totalBytes;
    private final double progress;
    private final long speed;
    private final Duration estimatedTimeRemaining;

    public ProgressEvent(long downloadedBytes, long totalBytes, long speed, Duration estimatedTimeRemaining) {
        this.downloadedBytes = downloadedBytes;
        this.totalBytes = totalBytes;
        this.progress = totalBytes > 0 ? (double) downloadedBytes / totalBytes : 0;
        this.speed = speed;
        this.estimatedTimeRemaining = estimatedTimeRemaining;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public double getProgress() {
        return progress;
    }

    public long getSpeed() {
        return speed;
    }

    public Duration getEstimatedTimeRemaining() {
        return estimatedTimeRemaining;
    }
}