package org.aurora.launcher.api.mojang;

import java.util.List;

public class VersionManifest {
    
    private LatestVersion latest;
    private List<VersionInfo> versions;
    
    public LatestVersion getLatest() {
        return latest;
    }
    
    public void setLatest(LatestVersion latest) {
        this.latest = latest;
    }
    
    public List<VersionInfo> getVersions() {
        return versions;
    }
    
    public void setVersions(List<VersionInfo> versions) {
        this.versions = versions;
    }
    
    public static class LatestVersion {
        private String release;
        private String snapshot;
        
        public String getRelease() {
            return release;
        }
        
        public void setRelease(String release) {
            this.release = release;
        }
        
        public String getSnapshot() {
            return snapshot;
        }
        
        public void setSnapshot(String snapshot) {
            this.snapshot = snapshot;
        }
    }
}