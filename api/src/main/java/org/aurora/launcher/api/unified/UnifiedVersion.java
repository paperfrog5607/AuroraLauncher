package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.List;

public class UnifiedVersion {
    
    private String id;
    private String source;
    private String versionNumber;
    private String name;
    private String changelog;
    private String type;
    private List<String> gameVersions;
    private List<String> loaders;
    private String datePublished;
    private List<DownloadFile> files;
    
    public UnifiedVersion() {
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getVersionNumber() {
        return versionNumber;
    }
    
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getChangelog() {
        return changelog;
    }
    
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    
    public String getDatePublished() {
        return datePublished;
    }
    
    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }
    
    public List<DownloadFile> getFiles() {
        return files;
    }
    
    public void setFiles(List<DownloadFile> files) {
        this.files = files != null ? files : new ArrayList<>();
    }
    
    public static class DownloadFile {
        private String url;
        private String filename;
        private long size;
        private String sha1;
        private boolean primary;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public void setFilename(String filename) {
            this.filename = filename;
        }
        
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }
        
        public String getSha1() {
            return sha1;
        }
        
        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }
        
        public boolean isPrimary() {
            return primary;
        }
        
        public void setPrimary(boolean primary) {
            this.primary = primary;
        }
    }
}