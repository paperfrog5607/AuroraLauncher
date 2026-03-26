package org.aurora.launcher.resource.resourcepack;

import java.awt.Image;
import java.nio.file.Path;
import java.util.List;

public class ResourcePack {
    
    private String id;
    private String name;
    private String description;
    private int packFormat;
    private Path filePath;
    private ResourcePackType type;
    private long fileSize;
    private String iconHash;
    private Image icon;
    private List<String> supportedVersions;
    
    public enum ResourcePackType {
        ZIP, FOLDER, SERVER
    }
    
    public ResourcePack() {
    }
    
    public ResourcePack(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public int getPackFormat() {
        return packFormat;
    }
    
    public void setPackFormat(int packFormat) {
        this.packFormat = packFormat;
    }
    
    public Path getFilePath() {
        return filePath;
    }
    
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    
    public ResourcePackType getType() {
        return type;
    }
    
    public void setType(ResourcePackType type) {
        this.type = type;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getIconHash() {
        return iconHash;
    }
    
    public void setIconHash(String iconHash) {
        this.iconHash = iconHash;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    public List<String> getSupportedVersions() {
        return supportedVersions;
    }
    
    public void setSupportedVersions(List<String> supportedVersions) {
        this.supportedVersions = supportedVersions;
    }
}