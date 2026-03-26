package org.aurora.launcher.modpack.backup;

import java.nio.file.Path;
import java.time.Instant;

public class Backup {
    
    public enum BackupType {
        FULL,
        INCREMENTAL,
        CONFIG_ONLY,
        WORLD_ONLY
    }
    
    private String id;
    private String instanceId;
    private String name;
    private String description;
    private Instant createdTime;
    private long size;
    private BackupType type;
    private Path backupFile;
    
    public Backup() {
        this.type = BackupType.FULL;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Instant getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public BackupType getType() {
        return type;
    }
    
    public void setType(BackupType type) {
        this.type = type;
    }
    
    public Path getBackupFile() {
        return backupFile;
    }
    
    public void setBackupFile(Path backupFile) {
        this.backupFile = backupFile;
    }
    
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}