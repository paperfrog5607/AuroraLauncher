package org.aurora.launcher.resource.datapack;

import java.nio.file.Path;
import java.util.List;

public class DataPack {
    
    private String id;
    private String name;
    private String description;
    private int packFormat;
    private Path filePath;
    private DataPackType type;
    private List<String> namespaces;
    private long fileSize;
    
    public enum DataPackType {
        ZIP, FOLDER, WORLD
    }
    
    public DataPack() {
    }
    
    public DataPack(String id, String name) {
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
    
    public DataPackType getType() {
        return type;
    }
    
    public void setType(DataPackType type) {
        this.type = type;
    }
    
    public List<String> getNamespaces() {
        return namespaces;
    }
    
    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}