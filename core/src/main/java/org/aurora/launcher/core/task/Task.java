package org.aurora.launcher.core.task;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Task<T> {
    protected String name;
    protected volatile float progress = 0f;
    protected volatile TaskState state = TaskState.PENDING;
    protected final AtomicBoolean cancelled = new AtomicBoolean(false);

    public Task(String name) {
        this.name = name;
    }

    public abstract T execute() throws TaskException;

    public void cancel() {
        cancelled.set(true);
        state = TaskState.CANCELLED;
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
    }

    public void updateProgress(long current, long total) {
        if (total > 0) {
            setProgress((float) current / total);
        }
    }

    public String getName() { return name; }
    public float getProgress() { return progress; }
    public TaskState getState() { return state; }
}