package org.aurora.launcher.api.modrinth;

public class ModrinthFile {
    
    private String url;
    private String filename;
    private boolean primary;
    private long size;
    private String sha1;
    private String sha512;
    
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
    
    public boolean isPrimary() {
        return primary;
    }
    
    public void setPrimary(boolean primary) {
        this.primary = primary;
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
    
    public String getSha512() {
        return sha512;
    }
    
    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }
}