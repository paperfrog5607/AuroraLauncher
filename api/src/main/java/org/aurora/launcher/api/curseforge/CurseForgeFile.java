package org.aurora.launcher.api.curseforge;

import java.util.ArrayList;
import java.util.List;

public class CurseForgeFile {
    
    private int id;
    private int modId;
    private String displayName;
    private String fileName;
    private String downloadUrl;
    private long fileLength;
    private String fileDate;
    private List<String> gameVersions;
    private long fileFingerprint;
    
    public CurseForgeFile() {
        this.gameVersions = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getModId() {
        return modId;
    }
    
    public void setModId(int modId) {
        this.modId = modId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public long getFileLength() {
        return fileLength;
    }
    
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
    
    public String getFileDate() {
        return fileDate;
    }
    
    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
    
    public List<String> getGameVersions() {
        return gameVersions;
    }
    
    public void setGameVersions(List<String> gameVersions) {
        this.gameVersions = gameVersions != null ? gameVersions : new ArrayList<>();
    }
    
    public long getFileFingerprint() {
        return fileFingerprint;
    }
    
    public void setFileFingerprint(long fileFingerprint) {
        this.fileFingerprint = fileFingerprint;
    }
}