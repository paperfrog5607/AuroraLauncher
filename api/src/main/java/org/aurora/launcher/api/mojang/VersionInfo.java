package org.aurora.launcher.api.mojang;

import java.util.ArrayList;
import java.util.List;

public class VersionInfo {
    
    private String id;
    private String type;
    private String url;
    private String time;
    private String releaseTime;
    private String sha1;
    private int complianceLevel;
    private List<Library> libraries;
    private MainClass mainClass;
    
    public VersionInfo() {
        this.libraries = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getReleaseTime() {
        return releaseTime;
    }
    
    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }
    
    public String getSha1() {
        return sha1;
    }
    
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
    
    public int getComplianceLevel() {
        return complianceLevel;
    }
    
    public void setComplianceLevel(int complianceLevel) {
        this.complianceLevel = complianceLevel;
    }
    
    public List<Library> getLibraries() {
        return libraries;
    }
    
    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries != null ? libraries : new ArrayList<>();
    }
    
    public MainClass getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    public static class Library {
        private String name;
        private Artifact download;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Artifact getDownload() {
            return download;
        }
        
        public void setDownload(Artifact download) {
            this.download = download;
        }
    }
    
    public static class Artifact {
        private String url;
        private String sha1;
        private long size;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getSha1() {
            return sha1;
        }
        
        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }
        
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }
    }
    
    public static class MainClass {
        private String client;
        private String server;
        
        public String getClient() {
            return client;
        }
        
        public void setClient(String client) {
            this.client = client;
        }
        
        public String getServer() {
            return server;
        }
        
        public void setServer(String server) {
            this.server = server;
        }
    }
}