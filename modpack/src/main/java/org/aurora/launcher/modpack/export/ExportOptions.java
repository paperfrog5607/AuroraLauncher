package org.aurora.launcher.modpack.export;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExportOptions {
    
    private boolean includeWorlds = false;
    private boolean includeServerPack = false;
    private boolean overrideExisting = false;
    private boolean skipUnknownMods = false;
    private Path outputPath;
    private String name;
    private String version;
    private String author;
    private List<String> excludedMods = new ArrayList<>();
    
    public ExportOptions() {
    }
    
    public boolean isIncludeWorlds() {
        return includeWorlds;
    }
    
    public void setIncludeWorlds(boolean includeWorlds) {
        this.includeWorlds = includeWorlds;
    }
    
    public boolean isIncludeServerPack() {
        return includeServerPack;
    }
    
    public void setIncludeServerPack(boolean includeServerPack) {
        this.includeServerPack = includeServerPack;
    }
    
    public boolean isOverrideExisting() {
        return overrideExisting;
    }
    
    public void setOverrideExisting(boolean overrideExisting) {
        this.overrideExisting = overrideExisting;
    }
    
    public boolean isSkipUnknownMods() {
        return skipUnknownMods;
    }
    
    public void setSkipUnknownMods(boolean skipUnknownMods) {
        this.skipUnknownMods = skipUnknownMods;
    }
    
    public Path getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
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
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public List<String> getExcludedMods() {
        return excludedMods;
    }
    
    public void setExcludedMods(List<String> excludedMods) {
        this.excludedMods = excludedMods != null ? excludedMods : new ArrayList<>();
    }
    
    public void addExcludedMod(String modId) {
        excludedMods.add(modId);
    }
}