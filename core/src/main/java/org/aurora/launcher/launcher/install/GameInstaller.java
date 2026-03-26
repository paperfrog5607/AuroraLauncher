package org.aurora.launcher.launcher.install;

import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.launcher.asset.AssetDownloader;
import org.aurora.launcher.launcher.asset.AssetManager;
import org.aurora.launcher.launcher.library.LibraryDownloader;
import org.aurora.launcher.launcher.library.LibraryValidator;
import org.aurora.launcher.launcher.version.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameInstaller {
    private final Path versionsDir;
    private final Path librariesDir;
    private final Path assetsDir;
    private final HttpClient httpClient;
    private final LibraryDownloader libraryDownloader;
    private final AssetDownloader assetDownloader;
    private final AssetManager assetManager;
    private final LibraryValidator libraryValidator;

    public GameInstaller(Path versionsDir, Path librariesDir, Path assetsDir) {
        this.versionsDir = versionsDir;
        this.librariesDir = librariesDir;
        this.assetsDir = assetsDir;
        this.httpClient = new HttpClient();
        this.libraryDownloader = new LibraryDownloader(librariesDir);
        this.assetDownloader = new AssetDownloader(assetsDir);
        this.assetManager = new AssetManager(assetsDir);
        this.libraryValidator = new LibraryValidator();
    }

    public CompletableFuture<Void> install(VersionInfo version, InstallOptions options, ProgressCallback callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (callback != null) callback.onMessage("Starting installation for " + version.getId());
                
                if (callback != null) callback.onProgress("client", 0, 0, 100);
                installClient(version);
                if (callback != null) callback.onProgress("client", 100, 100, 100);
                
                if (callback != null) callback.onMessage("Installing libraries...");
                installLibraries(version, callback);
                
                if (options.isIncludeAssets()) {
                    if (callback != null) callback.onMessage("Installing assets...");
                    installAssets(version, callback);
                }
                
                if (options.isIncludeNatives()) {
                    if (callback != null) callback.onMessage("Extracting natives...");
                    installNatives(version);
                }
                
                saveVersionJson(version);
                
                if (callback != null) {
                    callback.onMessage("Installation complete!");
                    callback.onComplete();
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
                throw new RuntimeException("Installation failed", e);
            }
        });
    }

    public CompletableFuture<Void> installClient(VersionInfo version, Path target) {
        return CompletableFuture.runAsync(() -> {
            String url = version.getClientDownloadUrl();
            if (url == null) return;
            
            try {
                httpClient.download(url, target, (org.aurora.launcher.core.net.ProgressCallback) null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download client", e);
            }
        });
    }

    private void installClient(VersionInfo version) throws IOException {
        Path versionDir = versionsDir.resolve(version.getId());
        Files.createDirectories(versionDir);
        
        Path clientJar = versionDir.resolve(version.getId() + ".jar");
        Files.deleteIfExists(clientJar);
        
        try {
            httpClient.download(version.getClientDownloadUrl(), clientJar, (org.aurora.launcher.core.net.ProgressCallback) null);
        } catch (Exception e) {
            throw new IOException("Failed to download client jar", e);
        }
    }

    private void installLibraries(VersionInfo version, ProgressCallback callback) {
        List<Library> libraries = version.getLibraries();
        if (libraries == null) return;
        
        int total = libraries.size();
        int current = 0;
        
        for (Library library : libraries) {
            if (!library.isAllowedOnCurrentPlatform()) {
                continue;
            }
            
            try {
                libraryDownloader.download(library).get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to download library: " + library.getName(), e);
            }
            
            current++;
            if (callback != null) {
                callback.onProgress("libraries", (double) current / total * 100, current, total);
            }
        }
    }

    private void installAssets(VersionInfo version, ProgressCallback callback) {
        AssetIndex assetIndex = version.getAssetIndex();
        if (assetIndex == null) return;
        
        try {
            assetDownloader.download(assetIndex).get();
            assetManager.saveIndex(assetIndex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download assets", e);
        }
    }

    private void installNatives(VersionInfo version) {
        List<Library> libraries = version.getLibraries();
        if (libraries == null) return;
        
        Path nativesDir = versionsDir.resolve(version.getId()).resolve("natives");
        
        for (Library library : libraries) {
            if (library.getNatives() == null) continue;
            
            String classifier = library.getNatives().getClassifierForCurrentOs();
            if (classifier == null) continue;
            
            LibraryDownloads downloads = library.getDownloads();
            if (downloads == null || downloads.getClassifiers() == null) continue;
            
            LibraryArtifact nativeArtifact = downloads.getClassifiers().get(classifier);
            if (nativeArtifact == null) continue;
            
            try {
                Path nativePath = librariesDir.resolve(nativeArtifact.getPath());
                if (Files.exists(nativePath)) {
                    extractNatives(nativePath, nativesDir, library.getExtract());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract natives", e);
            }
        }
    }

    private void extractNatives(Path nativeJar, Path targetDir, LibraryExtract extract) throws IOException {
        Files.createDirectories(targetDir);
        
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(nativeJar.toFile())) {
            java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
            
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (extract != null && extract.shouldExclude(name)) {
                    continue;
                }
                
                if (entry.isDirectory()) {
                    continue;
                }
                
                Path targetPath = targetDir.resolve(name);
                Files.createDirectories(targetPath.getParent());
                Files.copy(zip.getInputStream(entry), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void saveVersionJson(VersionInfo version) throws IOException {
        Path versionDir = versionsDir.resolve(version.getId());
        Path versionJson = versionDir.resolve(version.getId() + ".json");
        
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(version);
        
        Files.write(versionJson, json.getBytes());
    }

    public boolean isInstalled(String versionId) {
        Path versionDir = versionsDir.resolve(versionId);
        Path versionJson = versionDir.resolve(versionId + ".json");
        Path clientJar = versionDir.resolve(versionId + ".jar");
        return Files.exists(versionJson) && Files.exists(clientJar);
    }

    public CompletableFuture<Void> repair(String versionId) {
        return CompletableFuture.runAsync(() -> {
            throw new UnsupportedOperationException("Repair not implemented yet");
        });
    }

    public CompletableFuture<Void> uninstall(String versionId) {
        return CompletableFuture.runAsync(() -> {
            Path versionDir = versionsDir.resolve(versionId);
            if (Files.exists(versionDir)) {
                deleteDirectory(versionDir);
            }
        });
    }

    private void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException ignored) {
                    }
                });
        } catch (IOException ignored) {
        }
    }

    public Path getVersionsDir() {
        return versionsDir;
    }

    public Path getLibrariesDir() {
        return librariesDir;
    }

    public Path getAssetsDir() {
        return assetsDir;
    }
}