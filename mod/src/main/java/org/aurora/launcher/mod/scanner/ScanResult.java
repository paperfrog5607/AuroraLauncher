package org.aurora.launcher.mod.scanner;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ScanResult {
    
    private List<ModInfo> mods;
    private List<Path> disabledMods;
    private List<Path> invalidMods;
    private List<ScanError> errors;
    private Instant scanTime;
    
    public ScanResult() {
        this.mods = new ArrayList<>();
        this.disabledMods = new ArrayList<>();
        this.invalidMods = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
    
    public List<ModInfo> getMods() {
        return mods;
    }
    
    public void setMods(List<ModInfo> mods) {
        this.mods = mods != null ? mods : new ArrayList<>();
    }
    
    public void addMod(ModInfo mod) {
        mods.add(mod);
    }
    
    public List<Path> getDisabledMods() {
        return disabledMods;
    }
    
    public void setDisabledMods(List<Path> disabledMods) {
        this.disabledMods = disabledMods != null ? disabledMods : new ArrayList<>();
    }
    
    public void addDisabledMod(Path path) {
        disabledMods.add(path);
    }
    
    public List<Path> getInvalidMods() {
        return invalidMods;
    }
    
    public void setInvalidMods(List<Path> invalidMods) {
        this.invalidMods = invalidMods != null ? invalidMods : new ArrayList<>();
    }
    
    public void addInvalidMod(Path path) {
        invalidMods.add(path);
    }
    
    public List<ScanError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ScanError> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
    }
    
    public void addError(ScanError error) {
        errors.add(error);
    }
    
    public Instant getScanTime() {
        return scanTime;
    }
    
    public void setScanTime(Instant scanTime) {
        this.scanTime = scanTime;
    }
    
    public int getTotalCount() {
        return mods.size() + disabledMods.size();
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}