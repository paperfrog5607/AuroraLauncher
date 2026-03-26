package org.aurora.launcher.modpack.backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

public class BackupScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupScheduler.class);
    
    private final ScheduledExecutorService scheduler;
    private final BackupManager backupManager;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    public BackupScheduler(BackupManager backupManager) {
        this.backupManager = backupManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public void scheduleBackup(String instanceId, ScheduleConfig config) {
        cancelSchedule(instanceId);
        
        long initialDelay = calculateInitialDelay(config);
        long period = calculatePeriod(config);
        
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("Executing scheduled backup for instance: {}", instanceId);
                backupManager.createBackup(instanceId, config.getBackupOptions()).join();
            } catch (Exception e) {
                logger.error("Scheduled backup failed for instance {}: {}", instanceId, e.getMessage());
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        
        scheduledTasks.put(instanceId, future);
        logger.info("Scheduled backup for instance {} with interval {}", instanceId, config.getInterval());
    }
    
    private long calculateInitialDelay(ScheduleConfig config) {
        return 0;
    }
    
    private long calculatePeriod(ScheduleConfig config) {
        switch (config.getInterval()) {
            case HOURLY:
                return Duration.ofHours(1).toMillis();
            case DAILY:
                return Duration.ofDays(1).toMillis();
            case WEEKLY:
                return Duration.ofDays(7).toMillis();
            default:
                return Duration.ofDays(1).toMillis();
        }
    }
    
    public void cancelSchedule(String instanceId) {
        ScheduledFuture<?> future = scheduledTasks.remove(instanceId);
        if (future != null) {
            future.cancel(false);
            logger.info("Cancelled scheduled backup for instance: {}", instanceId);
        }
    }
    
    public void cancelAll() {
        for (String instanceId : scheduledTasks.keySet()) {
            cancelSchedule(instanceId);
        }
    }
    
    public boolean isScheduled(String instanceId) {
        ScheduledFuture<?> future = scheduledTasks.get(instanceId);
        return future != null && !future.isCancelled() && !future.isDone();
    }
    
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public static class ScheduleConfig {
        
        public enum Interval {
            HOURLY, DAILY, WEEKLY
        }
        
        private Interval interval = Interval.DAILY;
        private BackupOptions backupOptions = new BackupOptions();
        private Instant startTime;
        
        public ScheduleConfig() {
            this.startTime = Instant.now();
        }
        
        public Interval getInterval() {
            return interval;
        }
        
        public void setInterval(Interval interval) {
            this.interval = interval;
        }
        
        public BackupOptions getBackupOptions() {
            return backupOptions;
        }
        
        public void setBackupOptions(BackupOptions backupOptions) {
            this.backupOptions = backupOptions;
        }
        
        public Instant getStartTime() {
            return startTime;
        }
        
        public void setStartTime(Instant startTime) {
            this.startTime = startTime;
        }
    }
}