package org.aurora.launcher.mod.dependency;

public class ConflictInfo {
    
    public enum ConflictType {
        BREAKS, CONFLICT, VERSION_MISMATCH, DUPLICATE
    }
    
    private final String mod1;
    private final String mod2;
    private final ConflictType type;
    private final String reason;
    
    public ConflictInfo(String mod1, String mod2, ConflictType type, String reason) {
        this.mod1 = mod1;
        this.mod2 = mod2;
        this.type = type;
        this.reason = reason;
    }
    
    public String getMod1() {
        return mod1;
    }
    
    public String getMod2() {
        return mod2;
    }
    
    public ConflictType getType() {
        return type;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return String.format("Conflict[%s]: %s <-> %s (%s)", type, mod1, mod2, reason);
    }
}