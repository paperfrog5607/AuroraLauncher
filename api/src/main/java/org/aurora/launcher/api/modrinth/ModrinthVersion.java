package org.aurora.launcher.api.modrinth;

import java.util.ArrayList;
import java.util.List;

public class ModrinthVersion {
    
    private String id;
    private String projectId;
    private String name;
    private String versionNumber;
    private String changelog;
    private String versionType;
    private List<String> gameVersions;
    private List<String> loaders;
    private boolean featured;
    private String datePublished;
    private List<ModrinthFile> files;
    
    public ModrinthVersion() {
        this.gameVersions = new ArrayList<>();
        this.loaders = new ArrayList<>();
        this.files = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersionNumber() {
        return versionNumber;
    }
    
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
    
    public String getChangelog() {
        return changelog;
    }
    
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
    
    public String getVersionType() {
        return versionType;
    }
    
    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }
    
    public List<String> getGameVersions() {
        return gameVersions;
    }
    
    public void setGameVersions(List<String> gameVersions) {
        this.gameVersions = gameVersions != null ? gameVersions : new ArrayList<>();
    }
    
    public List<String> getLoaders() {
        return loaders;
    }
    
    public void setLoaders(List<String> loaders) {
        this.loaders = loaders != null ? loaders : new ArrayList<>();
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public String getDatePublished() {
        return datePublished;
    }
    
    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }
    
    public List<ModrinthFile> getFiles() {
        return files;
    }
    
    public void setFiles(List<ModrinthFile> files) {
        this.files = files != null ? files : new ArrayList<>();
    }
}