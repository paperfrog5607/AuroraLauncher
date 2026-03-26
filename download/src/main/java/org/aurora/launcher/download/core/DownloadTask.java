package org.aurora.launcher.download.core;

import org.aurora.launcher.download.progress.ProgressCallback;
import org.aurora.launcher.download.progress.ProgressEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DownloadTask {
    private final DownloadRequest request;
    private DownloadStatus status;
    private long downloadedBytes;
    private long totalBytes;
    private Instant startTime;
    private Instant endTime;
    private int retryCount;
    private final List<ProgressCallback> callbacks;

    public DownloadTask(DownloadRequest request) {
        this.request = request;
        this.status = DownloadStatus.PENDING;
        this.downloadedBytes = 0;
        this.totalBytes = 0;
        this.retryCount = 0;
        this.callbacks = new ArrayList<>();
    }

    public String getId() {
        return request.getId();
    }

    public DownloadRequest getRequest() {
        return request;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
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

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public int getPriority() {
        return request.getPriority();
    }

    public void addCallback(ProgressCallback callback) {
        callbacks.add(callback);
    }

    public void notifyCallbacks(ProgressEvent event) {
        for (ProgressCallback callback : callbacks) {
            if (event != null) {
                callback.onProgress(event);
            }
        }
    }

    public void pause() {
        if (status == DownloadStatus.DOWNLOADING) {
            status = DownloadStatus.PAUSED;
        }
    }

    public void resume() {
        if (status == DownloadStatus.PAUSED) {
            status = DownloadStatus.DOWNLOADING;
        }
    }

    public void cancel() {
        status = DownloadStatus.CANCELLED;
    }
}