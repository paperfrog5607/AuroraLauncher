package org.aurora.launcher.mod.search;

import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthVersion;
import org.aurora.launcher.api.modrinth.ModrinthFile;
import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.core.net.DownloadOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModDownloader {
    
    private final ModrinthClient modrinthClient;
    private final HttpClient httpClient;
    
    public ModDownloader() {
        this.modrinthClient = new ModrinthClient();
        this.httpClient = new HttpClient();
    }
    
    public ModDownloader(ModrinthClient modrinthClient) {
        this.modrinthClient = modrinthClient;
        this.httpClient = new HttpClient();
    }
    
    public CompletableFuture<Path> download(String projectId, String versionId, Path targetDir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(targetDir);
                
                ModrinthVersion version = modrinthClient.getVersion(versionId).join();
                
                if (version == null || version.getFiles().isEmpty()) {
                    throw new RuntimeException("Version not found or no files available");
                }
                
                ModrinthFile primaryFile = version.getFiles().stream()
                        .filter(ModrinthFile::isPrimary)
                        .findFirst()
                        .orElse(version.getFiles().get(0));
                
                Path targetFile = targetDir.resolve(primaryFile.getFilename());
                
                DownloadOptions options = new DownloadOptions();
                options.setTimeout(60000);
                options.setRetryCount(3);
                
                httpClient.download(primaryFile.getUrl(), targetFile, options);
                
                return targetFile;
            } catch (IOException e) {
                throw new RuntimeException("Failed to download mod", e);
            }
        });
    }
    
    public CompletableFuture<Path> downloadLatest(String projectId, Path targetDir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                java.util.List<ModrinthVersion> versions = modrinthClient.getVersions(projectId).join();
                
                if (versions == null || versions.isEmpty()) {
                    throw new RuntimeException("No versions found for project: " + projectId);
                }
                
                ModrinthVersion latest = versions.get(0);
                return download(projectId, latest.getId(), targetDir).join();
            } catch (Exception e) {
                throw new RuntimeException("Failed to download latest version", e);
            }
        });
    }
}