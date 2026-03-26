package org.aurora.launcher.mod.search;

import java.util.ArrayList;
import java.util.List;

public class ModSearchResult {
    
    private String id;
    private String slug;
    private String name;
    private String description;
    private String author;
    private String iconUrl;
    private long downloads;
    private String source;
    private List<String> categories;
    private String pageUrl;
    private List<String> versions;
    
    public ModSearchResult() {
        this.categories = new ArrayList<>();
        this.versions = new ArrayList<>();
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
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public long getDownloads() {
        return downloads;
    }
    
    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }
    
    public String getPageUrl() {
        return pageUrl;
    }
    
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    
    public List<String> getVersions() {
        return versions;
    }
    
    public void setVersions(List<String> versions) {
        this.versions = versions != null ? versions : new ArrayList<>();
    }
}