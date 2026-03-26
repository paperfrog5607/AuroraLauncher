package org.aurora.launcher.diagnostic.conflict;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ConflictReport {
    private List<Conflict> conflicts;
    private List<Warning> warnings;
    private Instant analysisTime;

    public ConflictReport() {
        this.conflicts = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.analysisTime = Instant.now();
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<Conflict> conflicts) {
        this.conflicts = conflicts != null ? conflicts : new ArrayList<>();
    }

    public void addConflict(Conflict conflict) {
        if (conflict != null) {
            conflicts.add(conflict);
        }
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Warning> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public void addWarning(Warning warning) {
        if (warning != null) {
            warnings.add(warning);
        }
    }

    public Instant getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(Instant analysisTime) {
        this.analysisTime = analysisTime;
    }

    public boolean hasCriticalConflicts() {
        return conflicts.stream()
            .anyMatch(c -> c.getSeverity() == Severity.CRITICAL);
    }

    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }

    public int getConflictCount() {
        return conflicts.size();
    }

    public List<Conflict> getConflictsBySeverity(Severity severity) {
        List<Conflict> result = new ArrayList<>();
        for (Conflict c : conflicts) {
            if (c.getSeverity() == severity) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Conflict> getConflictsInvolving(String modId) {
        List<Conflict> result = new ArrayList<>();
        for (Conflict c : conflicts) {
            if (c.isInvolving(modId)) {
                result.add(c);
            }
        }
        return result;
    }

    public static class Warning {
        private String modId;
        private String message;
        private Severity severity;

        public Warning(String modId, String message, Severity severity) {
            this.modId = modId;
            this.message = message;
            this.severity = severity;
        }

        public String getModId() {
            return modId;
        }

        public String getMessage() {
            return message;
        }

        public Severity getSeverity() {
            return severity;
        }
    }
}