package org.aurora.launcher.api.modrinth;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModrinthProject {
    
    @SerializedName("project_id")
    private String id;
    private String slug;
    private String title;
    private String description;
    @SerializedName("icon_url")
    private String iconUrl;
    private List<String> categories;
    @SerializedName("project_type")
    private String projectType;
    private String status;
    private long downloads;
    private long follows;
    @SerializedName("game_versions")
    private List<String> gameVersions;
    private List<String> loaders;
    @SerializedName("date_created")
    private String dateCreated;
    @SerializedName("date_modified")
    private String dateModified;
    
    public ModrinthProject() {
        this.categories = new ArrayList<>();
        this.gameVersions = new ArrayList<>();
        this.loaders = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getName() {
        return title;
    }
    
    public void setName(String name) {
        this.title = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }
    
    public String getProjectType() {
        return projectType;
    }
    
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getDownloads() {
        return downloads;
    }
    
    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }
    
    public long getFollows() {
        return follows;
    }
    
    public void setFollows(long follows) {
        this.follows = follows;
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
    
    public String getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    public String getDateModified() {
        return dateModified;
    }
    
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
}