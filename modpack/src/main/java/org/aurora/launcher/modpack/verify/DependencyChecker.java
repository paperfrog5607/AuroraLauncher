package org.aurora.launcher.modpack.verify;

import org.aurora.launcher.modpack.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class DependencyChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyChecker.class);
    
    private static final Map<String, Set<String>> LOADER_DEPENDENCIES = new HashMap<>();
    
    static {
        Set<String> fabricDeps = new HashSet<>(Arrays.asList("fabric", "fabricloader", "fabric-api"));
        LOADER_DEPENDENCIES.put("fabric", fabricDeps);
        
        Set<String> forgeDeps = new HashSet<>(Arrays.asList("forge", "fml"));
        LOADER_DEPENDENCIES.put("forge", forgeDeps);
        
        Set<String> quiltDeps = new HashSet<>(Arrays.asList("quilt", "quilt_loader", "qsl"));
        LOADER_DEPENDENCIES.put("quilt", quiltDeps);
        
        Set<String> neoForgeDeps = new HashSet<>(Arrays.asList("neoforge", "nf"));
        LOADER_DEPENDENCIES.put("neoforge", neoForgeDeps);
    }
    
    private static final Map<String, Set<String>> MOD_CONFLICTS = new HashMap<>();
    
    static {
        MOD_CONFLICTS.put("optifine", new HashSet<>(Arrays.asList("sodium", "iris")));
        MOD_CONFLICTS.put("sodium", new HashSet<>(Arrays.asList("optifine")));
        MOD_CONFLICTS.put("iris", new HashSet<>(Arrays.asList("optifine")));
    }
    
    public CompletableFuture<VerifyReport> checkDependencies(Instance instance) {
        return CompletableFuture.supplyAsync(() -> {
            VerifyReport report = new VerifyReport();
            report.setInstanceId(instance.getId());
            
            Set<String> installedMods = collectInstalledMods(instance);
            
            checkLoaderDependencies(instance, installedMods, report);
            checkModDependencies(instance, installedMods, report);
            checkModConflicts(instance, installedMods, report);
            
            report.calculateTotals();
            
            logger.info("Dependency check completed for instance {}: {} issues found", 
                    instance.getName(), report.getTotalIssues());
            
            return report;
        });
    }
    
    private Set<String> collectInstalledMods(Instance instance) {
        Set<String> mods = new HashSet<>();
        Path modsDir = instance.getModsDir();
        
        if (modsDir == null || !Files.exists(modsDir)) {
            return mods;
        }
        
        try (Stream<Path> files = Files.list(modsDir)) {
            files.filter(p -> p.toString().endsWith(".jar"))
                 .filter(p -> !p.toString().endsWith(".disabled"))
                 .forEach(jarFile -> {
                     String modId = extractModId(jarFile);
                     if (modId != null) {
                         mods.add(modId.toLowerCase());
                     }
                 });
        } catch (IOException e) {
            logger.error("Failed to list mods", e);
        }
        
        return mods;
    }
    
    private String extractModId(Path jarFile) {
        String fileName = jarFile.getFileName().toString();
        return fileName.replace(".jar", "").replaceAll("-[0-9].*", "");
    }
    
    private void checkLoaderDependencies(Instance instance, Set<String> installedMods, VerifyReport report) {
        String loaderType = instance.getConfig() != null ? 
                instance.getConfig().getLoaderType() : null;
        
        if (loaderType == null || "vanilla".equalsIgnoreCase(loaderType)) {
            return;
        }
        
        Set<String> required = LOADER_DEPENDENCIES.get(loaderType.toLowerCase());
        if (required == null) {
            return;
        }
        
        for (String dep : required) {
            boolean found = installedMods.stream()
                    .anyMatch(mod -> mod.contains(dep.toLowerCase()));
            
            if (!found && !dep.equals(loaderType.toLowerCase())) {
                VerifyReport.DependencyIssue issue = new VerifyReport.DependencyIssue(
                        "loader", dep, VerifyReport.IssueType.MISSING);
                issue.setMessage("Required loader dependency missing: " + dep);
                report.addMissingDependency(issue);
            }
        }
    }
    
    private void checkModDependencies(Instance instance, Set<String> installedMods, VerifyReport report) {
        Map<String, Set<String>> modDependencies = new HashMap<>();
        
        modDependencies.put("sodium", new HashSet<>(Arrays.asList("fabric-api", "fabric")));
        modDependencies.put("iris", new HashSet<>(Arrays.asList("sodium", "fabric-api")));
        modDependencies.put("litematica", new HashSet<>(Arrays.asList("malilib")));
        modDependencies.put("minihud", new HashSet<>(Arrays.asList("malilib")));
        
        for (Map.Entry<String, Set<String>> entry : modDependencies.entrySet()) {
            String modId = entry.getKey();
            
            if (installedMods.contains(modId)) {
                for (String dep : entry.getValue()) {
                    boolean found = installedMods.stream()
                            .anyMatch(mod -> mod.contains(dep.toLowerCase()));
                    
                    if (!found) {
                        VerifyReport.DependencyIssue issue = new VerifyReport.DependencyIssue(
                                modId, dep, VerifyReport.IssueType.MISSING);
                        issue.setMessage("Mod " + modId + " requires " + dep);
                        report.addMissingDependency(issue);
                    }
                }
            }
        }
    }
    
    private void checkModConflicts(Instance instance, Set<String> installedMods, VerifyReport report) {
        for (Map.Entry<String, Set<String>> entry : MOD_CONFLICTS.entrySet()) {
            String mod1 = entry.getKey();
            
            if (installedMods.contains(mod1)) {
                for (String mod2 : entry.getValue()) {
                    if (installedMods.contains(mod2)) {
                        VerifyReport.DependencyIssue issue = new VerifyReport.DependencyIssue(
                                mod1, mod2, VerifyReport.IssueType.CONFLICT);
                        issue.setMessage("Mod conflict detected: " + mod1 + " conflicts with " + mod2);
                        report.addConflictDependency(issue);
                    }
                }
            }
        }
    }
    
    public CompletableFuture<List<String>> suggestFixes(VerifyReport report) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> suggestions = new ArrayList<>();
            
            for (VerifyReport.DependencyIssue issue : report.getMissingDependencies()) {
                suggestions.add("Install missing dependency: " + issue.getRequiredMod());
            }
            
            for (VerifyReport.DependencyIssue issue : report.getConflictDependencies()) {
                suggestions.add("Remove one of conflicting mods: " + issue.getModId() + 
                        " or " + issue.getRequiredMod());
            }
            
            return suggestions;
        });
    }
}