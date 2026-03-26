package org.aurora.launcher.download.queue;

import org.aurora.launcher.download.core.DownloadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadQueue {
    private final PriorityBlockingQueue<PriorityTask> queue;
    private int maxConcurrent;
    private final Map<String, DownloadTask> activeTasks;
    private final AtomicInteger activeCount;

    public DownloadQueue(int maxConcurrent) {
        this.queue = new PriorityBlockingQueue<>();
        this.maxConcurrent = maxConcurrent;
        this.activeTasks = new ConcurrentHashMap<>();
        this.activeCount = new AtomicInteger(0);
    }

    public void submit(DownloadTask task) {
        queue.put(new PriorityTask(task, task.getPriority()));
    }

    public void submitAll(List<DownloadTask> tasks) {
        for (DownloadTask task : tasks) {
            submit(task);
        }
    }

    public void cancel(String taskId) {
        DownloadTask task = activeTasks.get(taskId);
        if (task != null) {
            task.cancel();
            activeTasks.remove(taskId);
            activeCount.decrementAndGet();
        } else {
            queue.removeIf(t -> t.getTaskId().equals(taskId));
        }
    }

    public void pause(String taskId) {
        DownloadTask task = activeTasks.get(taskId);
        if (task != null) {
            task.pause();
        }
    }

    public void resume(String taskId) {
        DownloadTask task = activeTasks.get(taskId);
        if (task != null) {
            task.resume();
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getActiveCount() {
        return activeCount.get();
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(int max) {
        this.maxConcurrent = max;
    }

    public List<DownloadTask> getActiveTasks() {
        return new ArrayList<>(activeTasks.values());
    }

    public List<DownloadTask> getQueuedTasks() {
        List<DownloadTask> tasks = new ArrayList<>();
        for (PriorityTask pt : queue) {
            tasks.add(pt.getTask());
        }
        return tasks;
    }

    public PriorityTask poll() {
        return queue.poll();
    }

    public void addActiveTask(String taskId, DownloadTask task) {
        activeTasks.put(taskId, task);
        activeCount.incrementAndGet();
    }

    public void removeActiveTask(String taskId) {
        activeTasks.remove(taskId);
        activeCount.decrementAndGet();
    }

    public void shutdown() {
        queue.clear();
        activeTasks.clear();
        activeCount.set(0);
    }
}