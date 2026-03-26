package org.aurora.launcher.ai.crash;

import java.util.ArrayList;
import java.util.List;

public class CrashContext {
    
    private String mcVersion;
    private String loader;
    private String loaderVersion;
    private List<String> installedMods;
    private String javaVersion;
    private int allocatedMemory;
    
    public CrashContext() {
        this.installedMods = new ArrayList<>();
    }
    
    public String getMcVersion() {
        return mcVersion;
    }
    
    public void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion;
    }
    
    public String getLoader() {
        return loader;
    }
    
    public void setLoader(String loader) {
        this.loader = loader;
    }
    
    public String getLoaderVersion() {
        return loaderVersion;
    }
    
    public void setLoaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
    }
    
    public List<String> getInstalledMods() {
        return installedMods;
    }
    
    public void addInstalledMod(String modId) {
        installedMods.add(modId);
    }
    
    public String getJavaVersion() {
        return javaVersion;
    }
    
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
    
    public int getAllocatedMemory() {
        return allocatedMemory;
    }
    
    public void setAllocatedMemory(int allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }
}