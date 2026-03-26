package org.aurora.launcher.diagnostic.conflict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictDatabase {
    private final Map<String, List<ConflictEntry>> entries;

    public ConflictDatabase() {
        this.entries = new HashMap<>();
        loadDefaultConflicts();
    }

    private void loadDefaultConflicts() {
        addKnownConflict("optifine", "sodium", ConflictType.INCOMPATIBLE, 
            "OptiFine and Sodium are incompatible", 
            "Use Sodium alone or OptiFine alone, not both.",
            Severity.CRITICAL);
        
        addKnownConflict("optifine", "iris", ConflictType.INCOMPATIBLE,
            "OptiFine and Iris may conflict",
            "Iris works with Sodium, use Iris+Sodium instead.",
            Severity.WARNING);
        
        addKnownConflict("sodium", "rubidium", ConflictType.DUPLICATE,
            "Sodium and Rubidium serve the same purpose",
            "Use only one of them. Sodium for Fabric, Rubidium for Forge.",
            Severity.WARNING);
        
        addKnownConflict("lithium", "lithium-forge", ConflictType.DUPLICATE,
            "Duplicate performance mod",
            "Use only the version for your mod loader.",
            Severity.WARNING);
    }

    public void addKnownConflict(String mod1, String mod2, ConflictType type, 
                                 String reason, String solution, Severity severity) {
        ConflictEntry entry = new ConflictEntry(mod2, type, reason, solution, severity);
        entries.computeIfAbsent(mod1.toLowerCase(), k -> new ArrayList<>()).add(entry);
        
        ConflictEntry reverseEntry = new ConflictEntry(mod1, type, reason, solution, severity);
        entries.computeIfAbsent(mod2.toLowerCase(), k -> new ArrayList<>()).add(reverseEntry);
    }

    public List<ConflictEntry> lookup(String modId) {
        if (modId == null) return Collections.emptyList();
        return entries.getOrDefault(modId.toLowerCase(), Collections.emptyList());
    }

    public void addEntry(String modId, ConflictEntry entry) {
        if (modId != null && entry != null) {
            entries.computeIfAbsent(modId.toLowerCase(), k -> new ArrayList<>()).add(entry);
        }
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }

    public static class ConflictEntry {
        private String conflictingMod;
        private ConflictType type;
        private String reason;
        private String solution;
        private Severity severity;

        public ConflictEntry(String conflictingMod, ConflictType type, 
                            String reason, String solution, Severity severity) {
            this.conflictingMod = conflictingMod;
            this.type = type;
            this.reason = reason;
            this.solution = solution;
            this.severity = severity;
        }

        public String getConflictingMod() {
            return conflictingMod;
        }

        public ConflictType getType() {
            return type;
        }

        public String getReason() {
            return reason;
        }

        public String getSolution() {
            return solution;
        }

        public Severity getSeverity() {
            return severity;
        }
    }
}