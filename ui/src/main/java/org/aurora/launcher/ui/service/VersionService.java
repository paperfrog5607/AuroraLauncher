package org.aurora.launcher.ui.service;

import org.aurora.launcher.api.mojang.MojangClient;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.api.mojang.VersionManifest;
import org.aurora.launcher.core.path.PathManager;
import org.aurora.launcher.download.config.DownloadConfig;
import org.aurora.launcher.download.core.DownloadEngine;
import org.aurora.launcher.download.core.DownloadRequest;
import org.aurora.launcher.download.core.DownloadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VersionService {
    private static final Logger logger = LoggerFactory.getLogger(VersionService.class);
    
    private final MojangClient mojangClient;
    private final DownloadEngine downloadEngine;
    private final Path versionsDir;
    private VersionManifest cachedManifest;
    
    public VersionService() {
        this.mojangClient = new MojangClient();
        this.downloadEngine = new DownloadEngine(new DownloadConfig());
        this.versionsDir = PathManager.getInstance().getVersionsDirectory();
    }
    
    public CompletableFuture<List<VersionInfo>> getVersionList() {
        if (cachedManifest != null) {
            return CompletableFuture.completedFuture(cachedManifest.getVersions());
        }
        
        return mojangClient.getVersionManifest()
            .thenApply(manifest -> {
                cachedManifest = manifest;
                logger.info("Loaded {} versions", manifest.getVersions().size());
                return manifest.getVersions();
            })
            .exceptionally(e -> {
                logger.error("Failed to load version manifest", e);
                return new ArrayList<>();
            });
    }
    
    public CompletableFuture<List<VersionInfo>> getReleaseVersions() {
        return getVersionList().thenApply(versions -> 
            versions.stream()
                .filter(v -> "release".equals(v.getType()))
                .collect(Collectors.toList())
        );
    }
    
    public CompletableFuture<List<VersionInfo>> getSnapshotVersions() {
        return getVersionList().thenApply(versions -> 
            versions.stream()
                .filter(v -> "snapshot".equals(v.getType()))
                .collect(Collectors.toList())
        );
    }
    
    public String getLatestRelease() {
        return cachedManifest != null && cachedManifest.getLatest() != null 
            ? cachedManifest.getLatest().getRelease() 
            : null;
    }
    
    public String getLatestSnapshot() {
        return cachedManifest != null && cachedManifest.getLatest() != null 
            ? cachedManifest.getLatest().getSnapshot() 
            : null;
    }
    
    public CompletableFuture<DownloadResult> downloadVersion(String versionId, 
            Consumer<Double> progressCallback) {
        return getVersionList().thenCompose(versions -> {
            for (VersionInfo info : versions) {
                if (info.getId().equals(versionId)) {
                    return downloadVersionJson(info, progressCallback);
                }
            }
            CompletableFuture<DownloadResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Version not found: " + versionId));
            return failed;
        });
    }
    
    private CompletableFuture<DownloadResult> downloadVersionJson(VersionInfo versionInfo, 
            Consumer<Double> progressCallback) {
        try {
            Path versionDir = versionsDir.resolve(versionInfo.getId());
            Path jsonFile = versionDir.resolve(versionInfo.getId() + ".json");
            
            DownloadRequest request = new DownloadRequest();
            request.setUrl(versionInfo.getUrl());
            request.setTargetPath(jsonFile);
            request.setExpectedSha1(versionInfo.getSha1());
            
            logger.info("Downloading version {} to {}", versionInfo.getId(), jsonFile);
            
            return downloadEngine.download(request)
                .thenApply(result -> {
                    if (result.getStatus() == org.aurora.launcher.download.core.DownloadStatus.COMPLETED) {
                        logger.info("Downloaded version {} successfully", versionInfo.getId());
                    } else {
                        logger.error("Failed to download version {}: {}", versionInfo.getId(), result.getError());
                    }
                    return result;
                });
        } catch (Exception e) {
            CompletableFuture<DownloadResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
    
    public boolean isVersionDownloaded(String versionId) {
        Path jsonFile = versionsDir.resolve(versionId).resolve(versionId + ".json");
        return jsonFile.toFile().exists();
    }
    
    public void shutdown() {
        mojangClient.close();
        downloadEngine.shutdown();
    }
}