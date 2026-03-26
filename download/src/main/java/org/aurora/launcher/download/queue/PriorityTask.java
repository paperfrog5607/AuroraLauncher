package org.aurora.launcher.download.queue;

import org.aurora.launcher.download.core.DownloadTask;

public class PriorityTask implements Comparable<PriorityTask> {
    private final DownloadTask task;
    private final int priority;

    public PriorityTask(DownloadTask task, int priority) {
        this.task = task;
        this.priority = priority;
    }

    public DownloadTask getTask() {
        return task;
    }

    public int getPriority() {
        return priority;
    }

    public String getTaskId() {
        return task.getId();
    }

    @Override
    public int compareTo(PriorityTask other) {
        return Integer.compare(other.priority, this.priority);
    }
}