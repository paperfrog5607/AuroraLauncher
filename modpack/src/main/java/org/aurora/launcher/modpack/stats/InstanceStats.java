package org.aurora.launcher.modpack.stats;

import java.util.HashMap;
import java.util.Map;

public class InstanceStats {
    
    private String instanceId;
    private String instanceName;
    private int modCount;
    private int resourcePackCount;
    private int shaderPackCount;
    private int worldCount;
    private long totalSize;
    private long modSize;
    private long configSize;
    private long worldSize;
    private long playTimeSeconds;
    private int launchCount;
    private Map<String, Long> modSizes;
    
    public InstanceStats() {
        this.modSizes = new HashMap<>();
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
    
    public int getModCount() {
        return modCount;
    }
    
    public void setModCount(int modCount) {
        this.modCount = modCount;
    }
    
    public int getResourcePackCount() {
        return resourcePackCount;
    }
    
    public void setResourcePackCount(int resourcePackCount) {
        this.resourcePackCount = resourcePackCount;
    }
    
    public int getShaderPackCount() {
        return shaderPackCount;
    }
    
    public void setShaderPackCount(int shaderPackCount) {
        this.shaderPackCount = shaderPackCount;
    }
    
    public int getWorldCount() {
        return worldCount;
    }
    
    public void setWorldCount(int worldCount) {
        this.worldCount = worldCount;
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    
    public long getModSize() {
        return modSize;
    }
    
    public void setModSize(long modSize) {
        this.modSize = modSize;
    }
    
    public long getConfigSize() {
        return configSize;
    }
    
    public void setConfigSize(long configSize) {
        this.configSize = configSize;
    }
    
    public long getWorldSize() {
        return worldSize;
    }
    
    public void setWorldSize(long worldSize) {
        this.worldSize = worldSize;
    }
    
    public long getPlayTimeSeconds() {
        return playTimeSeconds;
    }
    
    public void setPlayTimeSeconds(long playTimeSeconds) {
        this.playTimeSeconds = playTimeSeconds;
    }
    
    public int getLaunchCount() {
        return launchCount;
    }
    
    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }
    
    public Map<String, Long> getModSizes() {
        return modSizes;
    }
    
    public void setModSizes(Map<String, Long> modSizes) {
        this.modSizes = modSizes != null ? modSizes : new HashMap<>();
    }
    
    public void addModSize(String modName, long size) {
        modSizes.put(modName, size);
    }
    
    public String getFormattedTotalSize() {
        return formatSize(totalSize);
    }
    
    public String getFormattedPlayTime() {
        long hours = playTimeSeconds / 3600;
        long minutes = (playTimeSeconds % 3600) / 60;
        long seconds = playTimeSeconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    @Override
    public String toString() {
        return "InstanceStats{" +
                "instanceId='" + instanceId + '\'' +
                ", modCount=" + modCount +
                ", totalSize=" + getFormattedTotalSize() +
                ", playTime=" + getFormattedPlayTime() +
                '}';
    }
}