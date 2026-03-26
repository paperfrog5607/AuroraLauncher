package org.aurora.launcher.ai.script;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptContext {
    
    private String mcVersion;
    private String loader;
    private List<String> installedMods;
    private Map<String, Object> customVariables;
    
    public ScriptContext() {
        this.installedMods = new ArrayList<>();
        this.customVariables = new java.util.HashMap<>();
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
    
    public List<String> getInstalledMods() {
        return installedMods;
    }
    
    public void addInstalledMod(String modId) {
        installedMods.add(modId);
    }
    
    public Map<String, Object> getCustomVariables() {
        return customVariables;
    }
    
    public void setCustomVariable(String key, Object value) {
        customVariables.put(key, value);
    }
}