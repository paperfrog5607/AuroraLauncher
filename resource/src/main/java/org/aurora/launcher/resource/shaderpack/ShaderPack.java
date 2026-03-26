package org.aurora.launcher.resource.shaderpack;

import java.awt.Image;
import java.nio.file.Path;
import java.util.List;

public class ShaderPack {
    
    private String id;
    private String name;
    private String description;
    private Path filePath;
    private ShaderPackType type;
    private long fileSize;
    private Image icon;
    private List<String> supportedRenderers;
    
    public enum ShaderPackType {
        OPTIFINE, IRIS, VANILLA
    }
    
    public ShaderPack() {
    }
    
    public ShaderPack(String id, String name) {
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
    
    public Path getFilePath() {
        return filePath;
    }
    
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    
    public ShaderPackType getType() {
        return type;
    }
    
    public void setType(ShaderPackType type) {
        this.type = type;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    public List<String> getSupportedRenderers() {
        return supportedRenderers;
    }
    
    public void setSupportedRenderers(List<String> supportedRenderers) {
        this.supportedRenderers = supportedRenderers;
    }
}