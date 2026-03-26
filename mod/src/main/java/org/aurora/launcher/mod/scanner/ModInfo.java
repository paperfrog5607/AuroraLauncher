package org.aurora.launcher.mod.scanner;

import java.util.ArrayList;
import java.util.List;

public class ModInfo {
    
    public enum ModLoader {
        FABRIC, FORGE, QUILT, NEOFORGE
    }
    
    private String id;
    private String name;
    private String version;
    private String description;
    private List<String> authors;
    private String homepage;
    private String source;
    private String license;
    private List<Dependency> dependencies;
    private java.nio.file.Path filePath;
    private ModLoader loader;
    private String mcVersion;
    
    public ModInfo() {
        this.authors = new ArrayList<>();
        this.dependencies = new ArrayList<>();
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
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getAuthors() {
        return authors;
    }
    
    public void setAuthors(List<String> authors) {
        this.authors = authors != null ? authors : new ArrayList<>();
    }
    
    public void addAuthor(String author) {
        authors.add(author);
    }
    
    public String getHomepage() {
        return homepage;
    }
    
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getLicense() {
        return license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
    
    public List<Dependency> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
    }
    
    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }
    
    public java.nio.file.Path getFilePath() {
        return filePath;
    }
    
    public void setFilePath(java.nio.file.Path filePath) {
        this.filePath = filePath;
    }
    
    public ModLoader getLoader() {
        return loader;
    }
    
    public void setLoader(ModLoader loader) {
        this.loader = loader;
    }
    
    public String getMcVersion() {
        return mcVersion;
    }
    
    public void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion;
    }
}