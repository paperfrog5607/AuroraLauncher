package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnifiedMod {
    
    private String id;
    private String source;
    private String name;
    private String slug;
    private String description;
    private String iconUrl;
    private String author;
    private long downloads;
    private Map<String, Long> otherSourceDownloads = new HashMap<>();
    private List<String> categories;
    private List<String> gameVersions;
    private List<String> loaders;
    private String pageUrl;
    
    public UnifiedMod() {
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
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
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public long getDownloads() {
        return downloads;
    }
    
    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
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
    
    public String getPageUrl() {
        return pageUrl;
    }
    
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    
    public String getSourceName() {
        return source != null ? source.toLowerCase() : "unknown";
    }
    
    public String getFormattedDownloads() {
        long total = getTotalDownloads();
        if (total >= 1_000_000) {
            return String.format("%.1fM", total / 1_000_000.0);
        } else if (total >= 1_000) {
            return String.format("%.1fK", total / 1_000.0);
        }
        return String.valueOf(total);
    }
    
    public long getTotalDownloads() {
        long total = downloads;
        for (Long d : otherSourceDownloads.values()) {
            if (d != null) {
                total += d;
            }
        }
        return total;
    }
    
    public void addSourceDownload(String source, long downloadCount) {
        if (source != null && !source.equals(this.source)) {
            otherSourceDownloads.put(source, downloadCount);
        }
    }
    
    public Map<String, Long> getOtherSourceDownloads() {
        return otherSourceDownloads;
    }
    
    public boolean hasMultipleSources() {
        return !otherSourceDownloads.isEmpty();
    }
    
    public String getDisplaySources() {
        if (otherSourceDownloads.isEmpty()) {
            return source != null ? source.toUpperCase() : "UNKNOWN";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(source != null ? source.toUpperCase() : "UNKNOWN");
        for (String s : otherSourceDownloads.keySet()) {
            sb.append(" + ").append(s.toUpperCase());
        }
        return sb.toString();
    }
}