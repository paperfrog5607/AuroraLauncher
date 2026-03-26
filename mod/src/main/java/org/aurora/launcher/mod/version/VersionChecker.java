package org.aurora.launcher.mod.version;

import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthVersion;
import org.aurora.launcher.mod.scanner.ModInfo;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VersionChecker {
    
    private final ModrinthClient modrinthClient;
    
    public VersionChecker() {
        this.modrinthClient = new ModrinthClient();
    }
    
    public VersionChecker(ModrinthClient modrinthClient) {
        this.modrinthClient = modrinthClient;
    }
    
    public CompletableFuture<UpdateInfo> checkUpdate(ModInfo mod) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ModrinthVersion> versions = modrinthClient.getVersions(mod.getId()).join();
                
                if (versions == null || versions.isEmpty()) {
                    return null;
                }
                
                ModrinthVersion latest = versions.stream()
                        .filter(v -> "release".equals(v.getVersionType()))
                        .max(Comparator.comparing(v -> v.getDatePublished() != null ? v.getDatePublished() : ""))
                        .orElse(versions.get(0));
                
                String currentVersion = mod.getVersion();
                String latestVersion = latest.getVersionNumber();
                
                if (currentVersion != null && latestVersion != null) {
                    if (compareVersions(currentVersion, latestVersion) < 0) {
                        UpdateInfo updateInfo = new UpdateInfo();
                        updateInfo.setModId(mod.getId());
                        updateInfo.setCurrentVersion(currentVersion);
                        updateInfo.setLatestVersion(latestVersion);
                        updateInfo.setChangelog(latest.getChangelog());
                        updateInfo.setSource("modrinth");
                        
                        if (!latest.getFiles().isEmpty()) {
                            updateInfo.setDownloadUrl(latest.getFiles().get(0).getUrl());
                        }
                        
                        return updateInfo;
                    }
                }
                
                return null;
            } catch (Exception e) {
                return null;
            }
        });
    }
    
    public CompletableFuture<Map<String, UpdateInfo>> checkAllUpdates(List<ModInfo> mods) {
        Map<String, UpdateInfo> results = new HashMap<>();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ModInfo mod : mods) {
            CompletableFuture<Void> future = checkUpdate(mod).thenAccept(update -> {
                if (update != null) {
                    synchronized (results) {
                        results.put(mod.getId(), update);
                    }
                }
            });
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> results);
    }
    
    public int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("[.-]");
        String[] parts2 = v2.split("[.-]");
        
        int length = Math.max(parts1.length, parts2.length);
        
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;
            
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        
        return 0;
    }
    
    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}