package org.aurora.launcher.launcher.version;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VersionManager {
    private final VersionManifestService manifestService;
    private final Path versionsDir;

    public VersionManager(Path versionsDir) {
        this.versionsDir = versionsDir;
        this.manifestService = new VersionManifestService();
    }

    public VersionManager(Path versionsDir, VersionManifestService manifestService) {
        this.versionsDir = versionsDir;
        this.manifestService = manifestService;
    }

    public CompletableFuture<List<VersionInfo>> getAvailableVersions() {
        return manifestService.getManifest()
            .thenApply(VersionManifest::getVersions);
    }

    public CompletableFuture<List<VersionInfo>> getVersionsByType(VersionType type) {
        return manifestService.getVersions(type);
    }

    public CompletableFuture<VersionInfo> getLatestRelease() {
        return manifestService.getLatestRelease();
    }

    public CompletableFuture<VersionInfo> getLatestSnapshot() {
        return manifestService.getLatestSnapshot();
    }

    public CompletableFuture<VersionInfo> getVersionInfo(String versionId) {
        return manifestService.getVersionInfo(versionId);
    }

    public boolean isInstalled(String versionId) {
        Path versionDir = versionsDir.resolve(versionId);
        Path versionJson = versionDir.resolve(versionId + ".json");
        return versionJson.toFile().exists();
    }

    public Optional<Path> getVersionPath(String versionId) {
        Path versionDir = versionsDir.resolve(versionId);
        if (versionDir.toFile().exists()) {
            return Optional.of(versionDir);
        }
        return Optional.empty();
    }

    public Path getVersionsDir() {
        return versionsDir;
    }
}