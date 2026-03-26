package org.aurora.launcher.modpack.instance;

public class ModLoaderInfo {
    
    public enum LoaderType {
        VANILLA, FABRIC, FORGE, QUILT, NEOFORGE
    }
    
    private LoaderType type;
    private String version;
    
    public ModLoaderInfo() {
        this.type = LoaderType.VANILLA;
    }
    
    public ModLoaderInfo(LoaderType type, String version) {
        this.type = type;
        this.version = version;
    }
    
    public LoaderType getType() {
        return type;
    }
    
    public void setType(LoaderType type) {
        this.type = type;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public boolean requiresLoader() {
        return type != LoaderType.VANILLA;
    }
}