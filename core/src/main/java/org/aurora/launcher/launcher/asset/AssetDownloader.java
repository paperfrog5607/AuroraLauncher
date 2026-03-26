package org.aurora.launcher.launcher.asset;

import org.aurora.launcher.launcher.version.AssetIndex;
import org.aurora.launcher.launcher.version.AssetObject;
import org.aurora.launcher.core.net.HttpClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AssetDownloader {
    private static final String ASSET_BASE_URL = "https://resources.download.minecraft.net/";
    
    private final Path assetsDir;
    private final HttpClient httpClient;

    public AssetDownloader(Path assetsDir) {
        this.assetsDir = assetsDir;
        this.httpClient = new HttpClient();
    }

    public CompletableFuture<Void> download(AssetIndex assetIndex) {
        return CompletableFuture.runAsync(() -> {
            Map<String, AssetObject> objects = assetIndex.getObjects();
            if (objects == null) return;
            
            List<CompletableFuture<Void>> downloads = objects.entrySet().stream()
                .map(entry -> downloadAsset(entry.getValue()))
                .collect(Collectors.toList());
            
            CompletableFuture.allOf(downloads.toArray(new CompletableFuture[0])).join();
        });
    }

    private CompletableFuture<Void> downloadAsset(AssetObject asset) {
        return CompletableFuture.runAsync(() -> {
            String hash = asset.getHash();
            String prefixedPath = asset.getPrefixedPath();
            if (hash == null || prefixedPath == null) return;
            
            String url = ASSET_BASE_URL + prefixedPath;
            Path targetPath = assetsDir.resolve("objects").resolve(prefixedPath);
            
            if (Files.exists(targetPath)) {
                return;
            }
            
            try {
                Files.createDirectories(targetPath.getParent());
                httpClient.download(url, targetPath, (org.aurora.launcher.core.net.ProgressCallback) null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download asset: " + hash, e);
            }
        });
    }

    public Path getAssetsDir() {
        return assetsDir;
    }
}