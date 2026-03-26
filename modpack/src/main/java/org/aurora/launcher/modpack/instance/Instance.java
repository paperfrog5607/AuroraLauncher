package org.aurora.launcher.modpack.instance;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Instance {
    
    public enum InstanceState {
        READY, RUNNING, UPDATING, ERROR
    }
    
    private String id;
    private String name;
    private String version;
    private Path instanceDir;
    private InstanceConfig config;
    private ModLoaderInfo loader;
    private Instant createdTime;
    private Instant lastPlayed;
    private long playTime;
    private String iconPath;
    private List<String> tags;
    private InstanceState state;
    
    public Instance() {
        this.tags = new ArrayList<>();
        this.state = InstanceState.READY;
        this.playTime = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Path getInstanceDir() {
        return instanceDir;
    }
    
    public void setInstanceDir(Path instanceDir) {
        this.instanceDir = instanceDir;
    }
    
    public InstanceConfig getConfig() {
        return config;
    }
    
    public void setConfig(InstanceConfig config) {
        this.config = config;
    }
    
    public ModLoaderInfo getLoader() {
        return loader;
    }
    
    public void setLoader(ModLoaderInfo loader) {
        this.loader = loader;
    }
    
    public Instant getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
    
    public Instant getLastPlayed() {
        return lastPlayed;
    }
    
    public void setLastPlayed(Instant lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
    
    public long getPlayTime() {
        return playTime;
    }
    
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }
    
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public InstanceState getState() {
        return state;
    }
    
    public void setState(InstanceState state) {
        this.state = state;
    }
    
    public Path getMinecraftDir() {
        if (instanceDir == null) return null;
        return instanceDir.resolve(".minecraft");
    }
    
    public Path getModsDir() {
        Path mcDir = getMinecraftDir();
        return mcDir != null ? mcDir.resolve("mods") : null;
    }
    
    public Path getConfigDir() {
        Path mcDir = getMinecraftDir();
        return mcDir != null ? mcDir.resolve("config") : null;
    }
    
    public Path getSavesDir() {
        Path mcDir = getMinecraftDir();
        return mcDir != null ? mcDir.resolve("saves") : null;
    }
}