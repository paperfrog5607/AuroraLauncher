package org.aurora.launcher.config.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigComparison {
    
    private Map<String, Object> config1;
    private Map<String, Object> config2;
    private List<ConfigDiff> diffs;
    private int addedCount;
    private int removedCount;
    private int modifiedCount;
    
    public ConfigComparison() {
        this.diffs = new ArrayList<>();
    }
    
    public ConfigComparison(Map<String, Object> config1, Map<String, Object> config2, List<ConfigDiff> diffs) {
        this.config1 = config1;
        this.config2 = config2;
        this.diffs = diffs;
        this.addedCount = 0;
        this.removedCount = 0;
        this.modifiedCount = 0;
        
        for (ConfigDiff diff : diffs) {
            switch (diff.getType()) {
                case ADDED:
                    addedCount++;
                    break;
                case REMOVED:
                    removedCount++;
                    break;
                case MODIFIED:
                    modifiedCount++;
                    break;
            }
        }
    }
    
    public Map<String, Object> getConfig1() {
        return config1;
    }
    
    public void setConfig1(Map<String, Object> config1) {
        this.config1 = config1;
    }
    
    public Map<String, Object> getConfig2() {
        return config2;
    }
    
    public void setConfig2(Map<String, Object> config2) {
        this.config2 = config2;
    }
    
    public List<ConfigDiff> getDiffs() {
        return diffs;
    }
    
    public void setDiffs(List<ConfigDiff> diffs) {
        this.diffs = diffs;
    }
    
    public int getAddedCount() {
        return addedCount;
    }
    
    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }
    
    public int getRemovedCount() {
        return removedCount;
    }
    
    public void setRemovedCount(int removedCount) {
        this.removedCount = removedCount;
    }
    
    public int getModifiedCount() {
        return modifiedCount;
    }
    
    public void setModifiedCount(int modifiedCount) {
        this.modifiedCount = modifiedCount;
    }
    
    public boolean hasDifferences() {
        return !diffs.isEmpty();
    }
    
    public int getTotalDifferences() {
        return addedCount + removedCount + modifiedCount;
    }
}