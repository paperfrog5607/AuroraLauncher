package org.aurora.launcher.api.curseforge;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CurseForgeMod {
    
    private int id;
    private String name;
    private String slug;
    private String summary;
    private String websiteUrl;
    private int gameId;
    private int classId;
    private Logo logo;
    private List<CurseForgeCategory> categories;
    private long downloadCount;
    private List<CurseForgeFile> latestFiles;
    
    public CurseForgeMod() {
        this.categories = new ArrayList<>();
        this.latestFiles = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    
    public int getGameId() {
        return gameId;
    }
    
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    
    public int getClassId() {
        return classId;
    }
    
    public void setClassId(int classId) {
        this.classId = classId;
    }
    
    public String getLogoUrl() {
        return logo != null ? logo.url : null;
    }
    
    public void setLogoUrl(String logoUrl) {
        if (this.logo == null) {
            this.logo = new Logo();
        }
        this.logo.url = logoUrl;
    }
    
    public Logo getLogo() {
        return logo;
    }
    
    public void setLogo(Logo logo) {
        this.logo = logo;
    }
    
    public List<CurseForgeCategory> getCategories() {
        return categories;
    }
    
    public void setCategories(List<CurseForgeCategory> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }
    
    public long getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(long downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    public List<CurseForgeFile> getLatestFiles() {
        return latestFiles;
    }
    
    public void setLatestFiles(List<CurseForgeFile> latestFiles) {
        this.latestFiles = latestFiles != null ? latestFiles : new ArrayList<>();
    }
    
    public static class Logo {
        public int id;
        public String title;
        public String url;
        public String thumbnailUrl;
        
        public Logo() {}
    }
}