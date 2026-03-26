package org.aurora.launcher.diagnostic.conflict;

public class Conflict {
    private String mod1;
    private String mod2;
    private ConflictType type;
    private String reason;
    private String solution;
    private Severity severity;
    private String reference;

    public Conflict() {
    }

    public Conflict(String mod1, String mod2, ConflictType type, Severity severity) {
        this.mod1 = mod1;
        this.mod2 = mod2;
        this.type = type;
        this.severity = severity;
    }

    public String getMod1() {
        return mod1;
    }

    public void setMod1(String mod1) {
        this.mod1 = mod1;
    }

    public String getMod2() {
        return mod2;
    }

    public void setMod2(String mod2) {
        this.mod2 = mod2;
    }

    public ConflictType getType() {
        return type;
    }

    public void setType(ConflictType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isInvolving(String modId) {
        return modId != null && (modId.equals(mod1) || modId.equals(mod2));
    }

    @Override
    public String toString() {
        return String.format("Conflict[%s <-> %s]: %s (%s)", 
            mod1, mod2, type, severity);
    }
}