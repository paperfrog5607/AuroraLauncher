package org.aurora.launcher.modpack.backup;

import java.util.concurrent.CompletableFuture;

public class BackupTask {
    
    public enum TaskState {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
    
    private final String taskId;
    private final String instanceId;
    private final BackupOptions options;
    private volatile TaskState state;
    private volatile double progress;
    private volatile long bytesProcessed;
    private volatile long totalBytes;
    private volatile String errorMessage;
    
    public BackupTask(String instanceId, BackupOptions options) {
        this.taskId = java.util.UUID.randomUUID().toString();
        this.instanceId = instanceId;
        this.options = options;
        this.state = TaskState.PENDING;
        this.progress = 0.0;
        this.bytesProcessed = 0;
        this.totalBytes = 0;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public BackupOptions getOptions() {
        return options;
    }
    
    public TaskState getState() {
        return state;
    }
    
    public void setState(TaskState state) {
        this.state = state;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    public long getBytesProcessed() {
        return bytesProcessed;
    }
    
    public void setBytesProcessed(long bytesProcessed) {
        this.bytesProcessed = bytesProcessed;
        if (totalBytes > 0) {
            this.progress = (double) bytesProcessed / totalBytes;
        }
    }
    
    public long getTotalBytes() {
        return totalBytes;
    }
    
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isRunning() {
        return state == TaskState.RUNNING;
    }
    
    public boolean isCompleted() {
        return state == TaskState.COMPLETED;
    }
    
    public boolean isFailed() {
        return state == TaskState.FAILED;
    }
    
    public boolean isCancelled() {
        return state == TaskState.CANCELLED;
    }
    
    public void cancel() {
        if (state == TaskState.PENDING || state == TaskState.RUNNING) {
            state = TaskState.CANCELLED;
        }
    }
}