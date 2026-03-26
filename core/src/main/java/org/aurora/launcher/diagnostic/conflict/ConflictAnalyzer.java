package org.aurora.launcher.diagnostic.conflict;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConflictAnalyzer {
    private final ConflictDatabase database;

    public ConflictAnalyzer() {
        this.database = new ConflictDatabase();
    }

    public ConflictAnalyzer(ConflictDatabase database) {
        this.database = database != null ? database : new ConflictDatabase();
    }

    public ConflictReport analyze(List<String> modIds) {
        ConflictReport report = new ConflictReport();
        
        if (modIds == null || modIds.isEmpty()) {
            return report;
        }
        
        checkKnownConflicts(modIds, report);
        checkDuplicates(modIds, report);
        
        return report;
    }

    private void checkKnownConflicts(List<String> modIds, ConflictReport report) {
        Set<String> checkedPairs = new HashSet<>();
        
        for (String modId : modIds) {
            List<ConflictDatabase.ConflictEntry> conflicts = database.lookup(modId);
            
            for (ConflictDatabase.ConflictEntry entry : conflicts) {
                String other = entry.getConflictingMod().toLowerCase();
                
                if (modIds.stream().anyMatch(m -> m.equalsIgnoreCase(other))) {
                    String pairKey = createPairKey(modId, other);
                    
                    if (!checkedPairs.contains(pairKey)) {
                        checkedPairs.add(pairKey);
                        
                        Conflict conflict = new Conflict(
                            modId.toLowerCase(),
                            other,
                            entry.getType(),
                            entry.getSeverity()
                        );
                        conflict.setReason(entry.getReason());
                        conflict.setSolution(entry.getSolution());
                        
                        report.addConflict(conflict);
                    }
                }
            }
        }
    }

    private void checkDuplicates(List<String> modIds, ConflictReport report) {
        Set<String> seen = new HashSet<>();
        
        for (String modId : modIds) {
            String lower = modId.toLowerCase();
            
            if (seen.contains(lower)) {
                Conflict conflict = new Conflict(
                    modId,
                    modId,
                    ConflictType.DUPLICATE,
                    Severity.WARNING
                );
                conflict.setReason("Duplicate mod detected: " + modId);
                conflict.setSolution("Remove one of the duplicate copies.");
                report.addConflict(conflict);
            } else {
                seen.add(lower);
            }
        }
    }

    private String createPairKey(String mod1, String mod2) {
        String a = mod1.toLowerCase();
        String b = mod2.toLowerCase();
        return a.compareTo(b) < 0 ? a + "|" + b : b + "|" + a;
    }

    public boolean hasConflict(String modId, List<String> installedMods) {
        if (modId == null || installedMods == null) {
            return false;
        }
        
        List<ConflictDatabase.ConflictEntry> conflicts = database.lookup(modId);
        
        for (ConflictDatabase.ConflictEntry entry : conflicts) {
            String other = entry.getConflictingMod();
            if (installedMods.stream().anyMatch(m -> m.equalsIgnoreCase(other))) {
                return true;
            }
        }
        
        return false;
    }

    public List<String> getConflictingMods(String modId, List<String> installedMods) {
        List<String> conflicting = new ArrayList<>();
        
        if (modId == null || installedMods == null) {
            return conflicting;
        }
        
        List<ConflictDatabase.ConflictEntry> conflicts = database.lookup(modId);
        
        for (ConflictDatabase.ConflictEntry entry : conflicts) {
            String other = entry.getConflictingMod();
            if (installedMods.stream().anyMatch(m -> m.equalsIgnoreCase(other))) {
                conflicting.add(other);
            }
        }
        
        return conflicting;
    }

    public ConflictDatabase getDatabase() {
        return database;
    }
}