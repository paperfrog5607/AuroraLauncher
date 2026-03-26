package org.aurora.launcher.modpack.template;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Template {
    
    private String id;
    private String name;
    private String description;
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private List<String> tags;
    private List<String> defaultMods;
    private String iconPath;
    private Instant createdTime;
    private String author;
    
    public Template() {
        this.tags = new ArrayList<>();
        this.defaultMods = new ArrayList<>();
        this.loaderType = "vanilla";
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMinecraftVersion() {
        return minecraftVersion;
    }
    
    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }
    
    public String getLoaderType() {
        return loaderType;
    }
    
    public void setLoaderType(String loaderType) {
        this.loaderType = loaderType;
    }
    
    public String getLoaderVersion() {
        return loaderVersion;
    }
    
    public void setLoaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
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
    
    public List<String> getDefaultMods() {
        return defaultMods;
    }
    
    public void setDefaultMods(List<String> defaultMods) {
        this.defaultMods = defaultMods != null ? defaultMods : new ArrayList<>();
    }
    
    public void addDefaultMod(String modId) {
        if (!defaultMods.contains(modId)) {
            defaultMods.add(modId);
        }
    }
    
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    public Instant getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
}