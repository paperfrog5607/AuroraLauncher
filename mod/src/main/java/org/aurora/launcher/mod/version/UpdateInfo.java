package org.aurora.launcher.mod.version;

import java.time.Instant;

public class UpdateInfo {
    
    public enum VersionType {
        RELEASE, BETA, ALPHA
    }
    
    private String modId;
    private String currentVersion;
    private String latestVersion;
    private String changelog;
    private String downloadUrl;
    private String source;
    private Instant releaseDate;
    private VersionType versionType;
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
    
    public String getLatestVersion() {
        return latestVersion;
    }
    
    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
    
    public String getChangelog() {
        return changelog;
    }
    
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Instant getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public VersionType getVersionType() {
        return versionType;
    }
    
    public void setVersionType(VersionType versionType) {
        this.versionType = versionType;
    }
    
    public boolean hasUpdate() {
        return currentVersion != null && latestVersion != null && 
               !currentVersion.equals(latestVersion);
    }
}