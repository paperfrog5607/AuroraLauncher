package org.aurora.launcher.mod.version;

public class VersionRange {
    
    private String minVersion;
    private String maxVersion;
    private boolean includeMin;
    private boolean includeMax;
    
    public VersionRange() {
        this.includeMin = true;
        this.includeMax = true;
    }
    
    public boolean matches(String version) {
        if (version == null) return false;
        
        VersionComparator comparator = new VersionComparator();
        
        if (minVersion != null) {
            int cmp = comparator.compare(version, minVersion);
            if (includeMin) {
                if (cmp < 0) return false;
            } else {
                if (cmp <= 0) return false;
            }
        }
        
        if (maxVersion != null) {
            int cmp = comparator.compare(version, maxVersion);
            if (includeMax) {
                if (cmp > 0) return false;
            } else {
                if (cmp >= 0) return false;
            }
        }
        
        return true;
    }
    
    public String getMinVersion() {
        return minVersion;
    }
    
    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
    
    public String getMaxVersion() {
        return maxVersion;
    }
    
    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }
    
    public boolean isIncludeMin() {
        return includeMin;
    }
    
    public void setIncludeMin(boolean includeMin) {
        this.includeMin = includeMin;
    }
    
    public boolean isIncludeMax() {
        return includeMax;
    }
    
    public void setIncludeMax(boolean includeMax) {
        this.includeMax = includeMax;
    }
    
    public static VersionRange parse(String range) {
        VersionRange vr = new VersionRange();
        
        if (range == null || range.isEmpty()) {
            return vr;
        }
        
        if (range.startsWith(">=")) {
            vr.setMinVersion(range.substring(2).trim());
            vr.setIncludeMin(true);
        } else if (range.startsWith(">")) {
            vr.setMinVersion(range.substring(1).trim());
            vr.setIncludeMin(false);
        } else if (range.startsWith("<=")) {
            vr.setMaxVersion(range.substring(2).trim());
            vr.setIncludeMax(true);
        } else if (range.startsWith("<")) {
            vr.setMaxVersion(range.substring(1).trim());
            vr.setIncludeMax(false);
        } else {
            vr.setMinVersion(range.trim());
            vr.setMaxVersion(range.trim());
        }
        
        return vr;
    }
}