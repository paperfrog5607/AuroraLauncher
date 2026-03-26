package org.aurora.launcher.download.progress;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ProgressTracker {
    private final long totalBytes;
    private long downloadedBytes;
    private Instant startTime;
    private final List<ProgressCallback> callbacks;

    public ProgressTracker(long totalBytes) {
        this.totalBytes = totalBytes;
        this.downloadedBytes = 0;
        this.startTime = Instant.now();
        this.callbacks = new ArrayList<>();
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public double getProgress() {
        return totalBytes > 0 ? (double) downloadedBytes / totalBytes : 0;
    }

    public void update(long bytes) {
        this.downloadedBytes += bytes;
        notifyCallbacks();
    }

    public void setDownloadedBytes(long bytes) {
        this.downloadedBytes = bytes;
        notifyCallbacks();
    }

    public long getSpeed() {
        Duration elapsed = Duration.between(startTime, Instant.now());
        long elapsedMillis = elapsed.toMillis();
        if (elapsedMillis <= 0) {
            return 0;
        }
        return (downloadedBytes * 1000) / elapsedMillis;
    }

    public Duration getEstimatedTimeRemaining() {
        long speed = getSpeed();
        if (speed <= 0) {
            return Duration.ofMillis(Long.MAX_VALUE);
        }
        long remainingBytes = totalBytes - downloadedBytes;
        if (remainingBytes <= 0) {
            return Duration.ZERO;
        }
        return Duration.ofMillis((remainingBytes * 1000) / speed);
    }

    public void addCallback(ProgressCallback callback) {
        callbacks.add(callback);
    }

    private void notifyCallbacks() {
        ProgressEvent event = new ProgressEvent(
            downloadedBytes,
            totalBytes,
            getSpeed(),
            getEstimatedTimeRemaining()
        );
        for (ProgressCallback callback : callbacks) {
            callback.onProgress(event);
        }
    }

    public void reset() {
        this.downloadedBytes = 0;
        this.startTime = Instant.now();
    }
}