package org.aurora.launcher.mod.dependency;

import org.aurora.launcher.mod.scanner.Dependency;

import java.util.*;

public class DependencyTree {
    
    private final Map<String, List<Dependency>> dependencies;
    private final Map<String, List<String>> dependents;
    private final List<Dependency> missingDependencies;
    private final List<ConflictInfo> conflicts;
    
    public DependencyTree() {
        this.dependencies = new HashMap<>();
        this.dependents = new HashMap<>();
        this.missingDependencies = new ArrayList<>();
        this.conflicts = new ArrayList<>();
    }
    
    public void addDependency(String modId, Dependency dependency) {
        dependencies.computeIfAbsent(modId, k -> new ArrayList<>()).add(dependency);
        dependents.computeIfAbsent(dependency.getModId(), k -> new ArrayList<>()).add(modId);
    }
    
    public void addMissingDependency(String modId, Dependency dependency) {
        missingDependencies.add(dependency);
    }
    
    public void addConflict(ConflictInfo conflict) {
        conflicts.add(conflict);
    }
    
    public List<Dependency> getDependencies(String modId) {
        return dependencies.getOrDefault(modId, Collections.emptyList());
    }
    
    public List<String> getDependents(String modId) {
        return dependents.getOrDefault(modId, Collections.emptyList());
    }
    
    public boolean hasMissingDependencies() {
        return !missingDependencies.isEmpty();
    }
    
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }
    
    public List<Dependency> getMissingDependencies() {
        return missingDependencies;
    }
    
    public List<ConflictInfo> getConflicts() {
        return conflicts;
    }
    
    public Set<String> getAllModIds() {
        return dependencies.keySet();
    }
}