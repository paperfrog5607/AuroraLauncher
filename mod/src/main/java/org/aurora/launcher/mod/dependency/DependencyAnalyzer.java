package org.aurora.launcher.mod.dependency;

import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.Dependency;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DependencyAnalyzer {
    
    public CompletableFuture<DependencyTree> analyze(List<ModInfo> mods) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyTree tree = new DependencyTree();
            
            for (ModInfo mod : mods) {
                String modId = mod.getId();
                
                for (Dependency dep : mod.getDependencies()) {
                    tree.addDependency(modId, dep);
                    
                    if (dep.getType() == Dependency.DependencyType.DEPENDS) {
                        boolean found = mods.stream()
                                .anyMatch(m -> m.getId().equals(dep.getModId()));
                        if (!found) {
                            tree.addMissingDependency(modId, dep);
                        }
                    }
                }
            }
            
            detectConflicts(mods, tree);
            
            return tree;
        });
    }
    
    public List<Dependency> getMissingDependencies(List<ModInfo> mods) {
        List<Dependency> missing = new ArrayList<>();
        Set<String> installedIds = new HashSet<>();
        
        for (ModInfo mod : mods) {
            installedIds.add(mod.getId());
        }
        
        for (ModInfo mod : mods) {
            for (Dependency dep : mod.getDependencies()) {
                if (dep.getType() == Dependency.DependencyType.DEPENDS) {
                    if (!installedIds.contains(dep.getModId())) {
                        missing.add(dep);
                    }
                }
            }
        }
        
        return missing;
    }
    
    public List<ConflictInfo> getConflicts(List<ModInfo> mods) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        Map<String, ModInfo> modMap = new HashMap<>();
        
        for (ModInfo mod : mods) {
            modMap.put(mod.getId(), mod);
        }
        
        for (ModInfo mod : mods) {
            for (Dependency dep : mod.getDependencies()) {
                if (dep.getType() == Dependency.DependencyType.BREAKS ||
                    dep.getType() == Dependency.DependencyType.CONFLICTS) {
                    
                    ModInfo conflictMod = modMap.get(dep.getModId());
                    if (conflictMod != null) {
                        conflicts.add(new ConflictInfo(
                                mod.getId(),
                                dep.getModId(),
                                ConflictInfo.ConflictType.BREAKS,
                                "Declared as incompatible"
                        ));
                    }
                }
            }
        }
        
        Map<String, List<ModInfo>> duplicateCheck = new HashMap<>();
        for (ModInfo mod : mods) {
            duplicateCheck.computeIfAbsent(mod.getId(), k -> new ArrayList<>()).add(mod);
        }
        
        for (Map.Entry<String, List<ModInfo>> entry : duplicateCheck.entrySet()) {
            if (entry.getValue().size() > 1) {
                conflicts.add(new ConflictInfo(
                        entry.getKey(),
                        entry.getKey(),
                        ConflictInfo.ConflictType.DUPLICATE,
                        "Duplicate mod found"
                ));
            }
        }
        
        return conflicts;
    }
    
    private void detectConflicts(List<ModInfo> mods, DependencyTree tree) {
        List<ConflictInfo> conflicts = getConflicts(mods);
        for (ConflictInfo conflict : conflicts) {
            tree.addConflict(conflict);
        }
    }
}