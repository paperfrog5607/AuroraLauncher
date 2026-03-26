package org.aurora.launcher.modpack.share;

import java.time.Instant;

public class ShareCode {
    
    private String code;
    private String instanceId;
    private String instanceName;
    private Instant createdTime;
    private Instant expiresTime;
    private String format;
    private String downloadUrl;
    private long fileSize;
    
    public ShareCode() {
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    
    public Instant getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
    
    public Instant getExpiresTime() {
        return expiresTime;
    }
    
    public void setExpiresTime(Instant expiresTime) {
        this.expiresTime = expiresTime;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public boolean isExpired() {
        return expiresTime != null && Instant.now().isAfter(expiresTime);
    }
    
    public boolean isValid() {
        return code != null && !code.isEmpty() && downloadUrl != null && !isExpired();
    }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024));
        }
    }
    
    @Override
    public String toString() {
        return "ShareCode{" +
                "code='" + code + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", format='" + format + '\'' +
                ", expired=" + isExpired() +
                '}';
    }
}