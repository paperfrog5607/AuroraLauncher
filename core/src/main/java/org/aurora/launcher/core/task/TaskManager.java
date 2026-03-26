package org.aurora.launcher.core.task;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TaskManager {
    private final ExecutorService executor;
    private final ConcurrentMap<String, Task<?>> runningTasks = new ConcurrentHashMap<>();

    public TaskManager(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public TaskManager() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public <T> CompletableFuture<T> submit(Task<T> task) {
        runningTasks.put(task.getName(), task);
        task.state = TaskState.RUNNING;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                T result = task.execute();
                task.state = task.isCancelled() ? TaskState.CANCELLED : TaskState.COMPLETED;
                return result;
            } catch (TaskException e) {
                task.state = TaskState.FAILED;
                throw new CompletionException(e);
            } finally {
                runningTasks.remove(task.getName());
            }
        }, executor);
    }

    public List<Task<?>> getRunningTasks() {
        return runningTasks.values().stream().collect(Collectors.toList());
    }

    public void cancelAll() {
        runningTasks.values().forEach(Task::cancel);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}